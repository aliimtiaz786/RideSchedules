package com.ride.schedules.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RideSchedule {

  private Integer rideId;
  private LocalDate departureDate;
  private Integer segmentSequence;
  private Integer fromStopId;
  private Integer toStopId;
}
