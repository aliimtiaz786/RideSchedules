package com.ride.schedules.util;

import com.ride.schedules.model.StopDetailEnriched;
import com.ride.schedules.model.StopDetailEnrichedFlattened;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtil {

  public static InputStream getInputStream(String fileName) {
    var classloader = Thread.currentThread().getContextClassLoader();
    return classloader.getResourceAsStream(fileName);
  }

  public static List<StopDetailEnriched> csvToStopDetailsEnriched(InputStream is)
      throws IOException {
    try (var fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        var csvParser =
            new CSVParser(
                fileReader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

      List<StopDetailEnriched> stopDetails = new ArrayList<>();
      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {

        if (csvRecord.size() >= csvParser.getHeaderMap().size()) {

          var stopDetailEnriched =
              StopDetailEnriched.builder()
                  .rideId(Integer.parseInt(csvRecord.get("rideId")))
                  .departureDate(LocalDate.parse(csvRecord.get("departureDate")))
                  .segmentSequence(Integer.parseInt(csvRecord.get("segmentSequence")))
                  .fromStopId(Integer.parseInt(csvRecord.get("fromStopId")))
                  .toStopId(Integer.parseInt(csvRecord.get("toStopId")))
                  .fromStopName(csvRecord.get("fromStopName"))
                  .toStopName(csvRecord.get("toStopName"))
                  .build();
          stopDetails.add(stopDetailEnriched);
        }
      }
      return stopDetails;
    }
  }

  public static List<StopDetailEnrichedFlattened> csvToStopDetailsEnrichedFlattened(InputStream is)
      throws IOException {
    try (var fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        var csvParser =
            new CSVParser(
                fileReader,
                CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

      List<StopDetailEnrichedFlattened> stopDetails = new ArrayList<>();
      Iterable<CSVRecord> csvRecords = csvParser.getRecords();

      for (CSVRecord csvRecord : csvRecords) {

        if (csvRecord.size() >= csvParser.getHeaderMap().size()) {

          var stopDetailEnrichedFlatteneed =
              StopDetailEnrichedFlattened.builder()
                  .rideId(Integer.parseInt(csvRecord.get("rideId")))
                  .departureDate(LocalDate.parse(csvRecord.get("departureDate")))
                  .stopSequence(Integer.parseInt(csvRecord.get("stopSequence")))
                  .stopId(Integer.parseInt(csvRecord.get("stopId")))
                  .stopName(csvRecord.get("stopName"))
                  .build();
          stopDetails.add(stopDetailEnrichedFlatteneed);
        }
      }
      return stopDetails;
    }
  }
}
