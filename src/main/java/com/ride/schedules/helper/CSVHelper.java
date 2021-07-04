package com.ride.schedules.helper;

import com.ride.schedules.model.RideSchedule;
import com.ride.schedules.model.StopDetail;
import com.ride.schedules.model.StopDetailEnriched;
import com.ride.schedules.model.StopDetailEnrichedFlattened;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class CSVHelper {

  private static final String RIDE_ID_CSV_HEADER = "rideId";
  private static final String DEPARTURE_DATE_CSV_HEADER = "departureDate";
  private static final String FROM_STOP_ID_CSV_HEADER = "fromStopId";
  private static final String TO_STOP_ID_CSV_HEADER = "toStopId";
  private static final String SEGMENT_SEQ_CSV_HEADER = "segmentSequence";

  public static List<RideSchedule> csvToRideSchedules(InputStream is) throws IOException {
    try (var fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        var csvParser = getCsvParser(fileReader)) {

      List<RideSchedule> rideSchedules = new ArrayList<>();

      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {

        if (csvRecord.size() >= csvParser.getHeaderMap().size()) {

          var rideSchedule =
              RideSchedule.builder()
                  .rideId(Integer.parseInt(csvRecord.get(RIDE_ID_CSV_HEADER)))
                  .departureDate(LocalDate.parse(csvRecord.get(DEPARTURE_DATE_CSV_HEADER)))
                  .fromStopId(Integer.parseInt(csvRecord.get(FROM_STOP_ID_CSV_HEADER)))
                  .toStopId(Integer.parseInt(csvRecord.get(TO_STOP_ID_CSV_HEADER)))
                  .segmentSequence(Integer.parseInt(csvRecord.get(SEGMENT_SEQ_CSV_HEADER)))
                  .build();

          rideSchedules.add(rideSchedule);
        }
      }

      log.info("Successfully parsed RideSchedules {}", rideSchedules);
      return rideSchedules;
    } catch (IOException e) {
      log.error("Error occurred while parsing csv file to object", e);
      throw e;
    }
  }

  public static List<StopDetail> csvToStopDetails(InputStream is) throws IOException {
    try (var fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        var csvParser = getCsvParser(fileReader)) {

      List<StopDetail> stopDetails = new ArrayList<>();
      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {

        if (csvRecord.size() >= csvParser.getHeaderMap().size()) {

          var stopDetail =
              StopDetail.builder()
                  .stopId(Integer.parseInt(csvRecord.get("stopId")))
                  .stopName((csvRecord.get("stopName")))
                  .build();
          stopDetails.add(stopDetail);
        }
      }
      log.info("Successfully parsed StopDetails {}", stopDetails);
      return stopDetails;
    } catch (IOException e) {
      log.error("Error occurred while parsing csv file to object", e);
      throw e;
    }
  }

  public static void writeStopDetailEncriched(
      List<StopDetailEnriched> stopDetailEnrichedList, String fileName) throws IOException {

    try (var writer = Files.newBufferedWriter(Paths.get(fileName));
        var csvPrinter =
            new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.withHeader(
                    RIDE_ID_CSV_HEADER,
                    DEPARTURE_DATE_CSV_HEADER,
                    SEGMENT_SEQ_CSV_HEADER,
                    FROM_STOP_ID_CSV_HEADER,
                    TO_STOP_ID_CSV_HEADER,
                    "fromStopName",
                    "toStopName"))) {

      for (StopDetailEnriched stopDetailEnriched : stopDetailEnrichedList) {
        csvPrinter.printRecord(
            stopDetailEnriched.getRideId(),
            stopDetailEnriched.getDepartureDate(),
            stopDetailEnriched.getSegmentSequence(),
            stopDetailEnriched.getFromStopId(),
            stopDetailEnriched.getToStopId(),
            stopDetailEnriched.getFromStopName(),
            stopDetailEnriched.getToStopName());
      }

      csvPrinter.flush();
      log.info("Successfully written csv data to file path {}", fileName);
    } catch (IOException e) {
      log.error("Error occurred while writing csv file", e);
      throw e;
    }
  }

  public static void writeStopDetailEncrichedFlattened(
      Set<StopDetailEnrichedFlattened> stopDetailEnrichedFlattenedSet, String fileName)
      throws IOException {

    try (var writer = Files.newBufferedWriter(Paths.get(fileName));
        var csvPrinter =
            new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.withHeader(
                    RIDE_ID_CSV_HEADER,
                    DEPARTURE_DATE_CSV_HEADER,
                    "stopSequence",
                    "stopId",
                    "stopName"))) {

      for (StopDetailEnrichedFlattened stopDetailEnrichedFlattened :
          stopDetailEnrichedFlattenedSet) {
        csvPrinter.printRecord(
            stopDetailEnrichedFlattened.getRideId(),
            stopDetailEnrichedFlattened.getDepartureDate(),
            stopDetailEnrichedFlattened.getStopSequence(),
            stopDetailEnrichedFlattened.getStopId(),
            stopDetailEnrichedFlattened.getStopName());
      }

      csvPrinter.flush();
      log.info("Successfully written csv data to file path {}", fileName);
    } catch (IOException e) {
      log.error("Error occurred while writing csv file", e);
      throw e;
    }
  }

  private static CSVParser getCsvParser(BufferedReader fileReader) throws IOException {
    return new CSVParser(
        fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());
  }
}
