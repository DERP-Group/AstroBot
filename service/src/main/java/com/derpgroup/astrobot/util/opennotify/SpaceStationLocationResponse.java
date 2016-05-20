package com.derpgroup.astrobot.util.opennotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpaceStationLocationResponse extends OpenNotifyResponse {

  private long timestamp;
  private SpaceStationLocation spaceStationLocation;
  
  public long getTimestamp() {
    return timestamp;
  }
  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }

  @JsonProperty("iss_position")
  public SpaceStationLocation getSpaceStationLocation() {
    return spaceStationLocation;
  }

  @JsonProperty("iss_position")
  public void setSpaceStationLocation(SpaceStationLocation spaceStationLocation) {
    this.spaceStationLocation = spaceStationLocation;
  }

  @Override
  public String toString() {
    return "SpaceStationLocationResponse [timestamp=" + timestamp
        + ", spaceStationLocation=" + spaceStationLocation + "]";
  }
}
