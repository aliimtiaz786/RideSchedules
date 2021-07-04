package com.ride.schedules.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ride.schedules.util.TestUtil;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RideScheduleServiceTest {

  private static final String STOP_DETAILS_ENRICHED_CSV_PATH =
      "src/test/resources/stop_details_enriched.csv";
  private static final String STOP_DETAILS_ENRICHED_FLATTENED_CSV_PATH =
      "src/test/resources/stop_details_enriched_flattened.csv";

  @BeforeEach
  public void setUp() {
    BasicConfigurator.configure();
  }

  @Test
  void shouldWriteStopDetailsSuccessfully() throws IOException {

    RideScheduleService.runRideSchedulesJob(
        STOP_DETAILS_ENRICHED_CSV_PATH, STOP_DETAILS_ENRICHED_FLATTENED_CSV_PATH);
    var stopDetailEnrichedList =
        TestUtil.csvToStopDetailsEnriched(new FileInputStream(STOP_DETAILS_ENRICHED_CSV_PATH));

    assertThat(stopDetailEnrichedList).isNotNull().isNotEmpty();
  }

  @Test
  void shouldWriteStopDetailsFlattenedSuccessfully() throws IOException {

    RideScheduleService.runRideSchedulesJob(
        STOP_DETAILS_ENRICHED_CSV_PATH, STOP_DETAILS_ENRICHED_FLATTENED_CSV_PATH);

    var stopDetailEnrichedFlattenedList =
        TestUtil.csvToStopDetailsEnrichedFlattened(
            new FileInputStream(STOP_DETAILS_ENRICHED_FLATTENED_CSV_PATH));

    assertThat(stopDetailEnrichedFlattenedList).isNotNull().isNotEmpty();
  }
}
