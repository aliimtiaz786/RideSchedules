package com.ride.schedules.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class StopDetailEnrichedFlattened {
  private Integer rideId;
  private LocalDate departureDate;
  private Integer stopSequence;
  private Integer stopId;
  private String stopName;
}
