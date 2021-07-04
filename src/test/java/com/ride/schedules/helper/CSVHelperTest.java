package com.ride.schedules.helper;

import static com.ride.schedules.util.TestUtil.csvToStopDetailsEnriched;
import static com.ride.schedules.util.TestUtil.csvToStopDetailsEnrichedFlattened;
import static com.ride.schedules.util.TestUtil.getInputStream;
import static org.assertj.core.api.Assertions.assertThat;

import com.ride.schedules.model.RideSchedule;
import com.ride.schedules.model.StopDetail;
import com.ride.schedules.model.StopDetailEnriched;
import com.ride.schedules.model.StopDetailEnrichedFlattened;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CSVHelperTest {

  @BeforeEach
  public void setUp() {
    BasicConfigurator.configure();
  }

  @Test
  void shouldParseRideSchedulesSuccessfully() throws IOException {

    var rideSchedules = CSVHelper.csvToRideSchedules(getInputStream("ride_schedule.csv"));
    assertThat(rideSchedules).isNotNull().isNotEmpty().hasSize(5);
    assertThat(rideSchedules.get(0))
        .extracting(
            RideSchedule::getRideId,
            RideSchedule::getDepartureDate,
            RideSchedule::getSegmentSequence,
            RideSchedule::getFromStopId,
            RideSchedule::getToStopId)
        .containsExactly(124603531, LocalDate.parse("2021-06-02"), 1, 2425, 12448);
  }

  @Test
  void shouldParseStopDetailsSuccessfully() throws IOException {

    var stopDetails = CSVHelper.csvToStopDetails(getInputStream("stop_details.csv"));
    assertThat(stopDetails).isNotNull().isNotEmpty().hasSize(6);
    assertThat(stopDetails.get(0))
        .extracting(StopDetail::getStopId, StopDetail::getStopName)
        .containsExactly(2425, "Rome Tiburtina Bus station");
  }

  @Test
  void shouldWriteStopDetailsEnriched() throws IOException {

    /*
     * 124603531,2021-06-02,1,2425,12448,Rome Tiburtina Bus station,Avezzano
     * */

    var stop1 =
        StopDetailEnriched.builder()
            .rideId(124603531)
            .departureDate(LocalDate.parse("2021-06-02"))
            .segmentSequence(1)
            .fromStopId(2425)
            .toStopId(12448)
            .fromStopName("Rome Tiburtina Bus station")
            .toStopName("Avezzano")
            .build();

    CSVHelper.writeStopDetailEncriched(
        List.of(stop1), "src/test/resources/stop_details_enriched_csv_helper_test.csv");
    var stopDetailsEnriched =
        csvToStopDetailsEnriched(getInputStream("stop_details_enriched_csv_helper_test.csv"));
    assertThat(stopDetailsEnriched).isNotNull().isNotEmpty().hasSize(1);
    assertThat(stopDetailsEnriched.get(0)).isEqualTo(stop1);
  }

  @Test
  void shouldWriteStopDetailsEnrichedFlattened() throws IOException {

    var stop1 =
        StopDetailEnrichedFlattened.builder()
            .rideId(124603531)
            .departureDate(LocalDate.parse("2021-06-02"))
            .stopSequence(1)
            .stopId(2425)
            .stopName("Rome Tiburtina Bus station")
            .build();

    CSVHelper.writeStopDetailEncrichedFlattened(
        Set.of(stop1), "src/test/resources/stop_details_enriched_flattened_csv_helper_test.csv");
    var stopDetailsEnriched =
        csvToStopDetailsEnrichedFlattened(
            getInputStream("stop_details_enriched_flattened_csv_helper_test.csv"));
    assertThat(stopDetailsEnriched).isNotNull().isNotEmpty().hasSize(1);
    assertThat(stopDetailsEnriched.get(0)).isEqualTo(stop1);
  }
}
