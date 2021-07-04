package com.ride.schedules.service;

import com.ride.schedules.helper.CSVHelper;
import com.ride.schedules.model.RideSchedule;
import com.ride.schedules.model.StopDetail;
import com.ride.schedules.model.StopDetailEnriched;
import com.ride.schedules.model.StopDetailEnrichedFlattened;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class RideScheduleService {

  private static final String STOP_DETAILS_CSV_PATH = "stop_details.csv";
  private static final String RIDE_SCHEDULES_CSV_PATH = "ride_schedule.csv";

  public static void runRideSchedulesJob(
      String stopDetailsEnrichedOutputFilePath, String stopDetailsEnrichedFlattenedOutputFilePath)
      throws IOException {
    List<RideSchedule> rideSchedules = getRideSchedules();
    List<StopDetail> stopDetails = getStopDetails();
    Map<Integer, String> stopDetailsMap = getStopDetailsMap(stopDetails);
    List<StopDetailEnriched> stopDetailEnrichedList =
        writeStopDetailsEncriched(rideSchedules, stopDetailsMap, stopDetailsEnrichedOutputFilePath);
    writeStopDetailsEnrichedFlattened(
        stopDetailEnrichedList, stopDetailsEnrichedFlattenedOutputFilePath);
  }

  private static List<StopDetailEnriched> writeStopDetailsEncriched(
      List<RideSchedule> rideSchedules,
      Map<Integer, String> stopDetailsMap,
      String stopDetailsEnrichedOutputFilePath)
      throws IOException {
    List<StopDetailEnriched> stopDetailEnrichedList =
        getStopDetailEnrichedList(rideSchedules, stopDetailsMap);
    CSVHelper.writeStopDetailEncriched(stopDetailEnrichedList, stopDetailsEnrichedOutputFilePath);
    cleanUp(rideSchedules, stopDetailsMap);
    log.info(
        "Finished writing stop details enriched to path {}", stopDetailsEnrichedOutputFilePath);
    return stopDetailEnrichedList;
  }

  private static void writeStopDetailsEnrichedFlattened(
      List<StopDetailEnriched> stopDetailEnrichedList,
      String stopDetailsEnrichedFlattenedOutputFilePath)
      throws IOException {
    Set<StopDetailEnrichedFlattened> stopDetailEnrichedFlattenedSet = new LinkedHashSet<>();

    populateStopDetailsEnrichedFlattened(stopDetailEnrichedList, stopDetailEnrichedFlattenedSet);
    CSVHelper.writeStopDetailEncrichedFlattened(
        stopDetailEnrichedFlattenedSet, stopDetailsEnrichedFlattenedOutputFilePath);
    log.info(
        "Finished writing stop details enriched flattened to path {}",
        stopDetailsEnrichedFlattenedOutputFilePath);
  }

  private static void cleanUp(
      List<RideSchedule> rideSchedules, Map<Integer, String> stopDetailsMap) {
    log.info("cleaning up unused memory data ");
    rideSchedules.clear();
    stopDetailsMap.clear();
  }

  private static void populateStopDetailsEnrichedFlattened(
      List<StopDetailEnriched> stopDetailEnrichedList,
      Set<StopDetailEnrichedFlattened> stopDetailEnrichedFlattenedSet) {
    LinkedHashMap<Integer, List<StopDetailEnriched>> stopDetailEnrichedMap =
        getStopDetailEnrichedMap(stopDetailEnrichedList);

    for (var stopDetailEnrichedEntry : stopDetailEnrichedMap.entrySet()) {
      var stopDetailEnrichedEntryValueList = stopDetailEnrichedEntry.getValue();
      for (var i = 0; i < stopDetailEnrichedEntryValueList.size(); i++) {
        var stopDetailEnriched = stopDetailEnrichedEntryValueList.get(i);
        var departureStop =
            StopDetailEnrichedFlattened.builder()
                .rideId(stopDetailEnriched.getRideId())
                .stopId(stopDetailEnriched.getFromStopId())
                .stopName(stopDetailEnriched.getFromStopName())
                .departureDate(stopDetailEnriched.getDepartureDate())
                .stopSequence(stopDetailEnriched.getSegmentSequence())
                .build();
        stopDetailEnrichedFlattenedSet.add(departureStop);

        // if its a last arrival stop we need to make to new stop entry
        if (i == stopDetailEnrichedEntryValueList.size() - 1) {
          var arrivalStops =
              StopDetailEnrichedFlattened.builder()
                  .rideId(stopDetailEnriched.getRideId())
                  .stopId(stopDetailEnriched.getToStopId())
                  .stopName(stopDetailEnriched.getToStopName())
                  .departureDate(stopDetailEnriched.getDepartureDate())
                  .stopSequence(stopDetailEnriched.getSegmentSequence() + 1)
                  .build();
          stopDetailEnrichedFlattenedSet.add(arrivalStops);
        }
      }
    }
  }

  private static LinkedHashMap<Integer, List<StopDetailEnriched>> getStopDetailEnrichedMap(
      List<StopDetailEnriched> stopDetailEnrichedList) {
    // group by rideId to get list of stop details based on rideId
    return stopDetailEnrichedList.stream()
        .sorted(Comparator.comparing(StopDetailEnriched::getRideId))
        .collect(
            Collectors.groupingBy(
                StopDetailEnriched::getRideId, LinkedHashMap::new, Collectors.toList()));
  }

  private static List<StopDetailEnriched> getStopDetailEnrichedList(
      List<RideSchedule> rideSchedules, Map<Integer, String> stopDetailsMap) {
    return rideSchedules.stream()
        .map(
            rideSchedule ->
                StopDetailEnriched.builder()
                    .rideId(rideSchedule.getRideId())
                    .departureDate(rideSchedule.getDepartureDate())
                    .fromStopId(rideSchedule.getFromStopId())
                    .toStopId(rideSchedule.getToStopId())
                    .segmentSequence(rideSchedule.getSegmentSequence())
                    .fromStopName(getStopName(stopDetailsMap, rideSchedule.getFromStopId()))
                    .toStopName(getStopName(stopDetailsMap, rideSchedule.getToStopId()))
                    .build())
        .collect(Collectors.toList());
  }

  private static Map<Integer, String> getStopDetailsMap(List<StopDetail> stopDetails) {
    return stopDetails.stream()
        .map(p -> Map.entry(p.getStopId(), p.getStopName()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
  }

  private static List<StopDetail> getStopDetails() throws IOException {
    // parse stop details csv
    return CSVHelper.csvToStopDetails(getInputStream(STOP_DETAILS_CSV_PATH));
  }

  private static InputStream getInputStream(String fileName) {
    var classloader = Thread.currentThread().getContextClassLoader();
    return classloader.getResourceAsStream(fileName);
  }

  private static List<RideSchedule> getRideSchedules() throws IOException {
    // parse ride schedules csv
    return CSVHelper.csvToRideSchedules(getInputStream(RIDE_SCHEDULES_CSV_PATH));
  }

  // found 41 stop missing in the csv data so I replaced with missing stop instead of null or
  // filtering it
  private static String getStopName(Map<Integer, String> stopDetailsMap, Integer stopId) {
    return Optional.ofNullable(stopDetailsMap.get(stopId)).orElse("MissingStop");
  }
}
