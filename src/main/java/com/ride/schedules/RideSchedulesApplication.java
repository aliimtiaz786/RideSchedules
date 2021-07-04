package com.ride.schedules;

import com.ride.schedules.service.RideScheduleService;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;

@Slf4j
public class RideSchedulesApplication {

  private static final String STOP_DETAILS_ENRICHED_CSV_PATH =
      "stop_details_enriched.csv";
  private static final String STOP_DETAILS_ENRICHED_FLATTENED_CSV_PATH =
      "stop_details_enriched_flattened.csv";

  public static void main(String[] args) throws IOException {

    BasicConfigurator.configure();
    log.info("Starting ride schedules job..");
    RideScheduleService.runRideSchedulesJob(
        STOP_DETAILS_ENRICHED_CSV_PATH, STOP_DETAILS_ENRICHED_FLATTENED_CSV_PATH);
    log.info("Finished ride schedules job..");
  }
}
