package com.derpgroup.astrobot;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type", defaultImpl = AstroBotMetadata.class)
public class AstroBotMetadata extends CommonMetadata {
  
  private int upcomingLaunchesIndex;

  public int getUpcomingLaunchesIndex() {
    return upcomingLaunchesIndex;
  }

  public void setUpcomingLaunchesIndex(int upcomingLaunchesIndex) {
    this.upcomingLaunchesIndex = upcomingLaunchesIndex;
  }
}
