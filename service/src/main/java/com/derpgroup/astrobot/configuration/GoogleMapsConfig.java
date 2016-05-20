package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class GoogleMapsConfig {

  @NotNull
  private String apiKey;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }
}
