package com.derpgroup.astrobot.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AstroBotConfig {

  @Valid
  @NotNull
  private OpenNotifyConfig openNotifyConfig;
  
  @Valid
  @NotNull
  private LaunchLibraryConfig launchLibraryConfig;
  
  @Valid
  @NotNull
  private GoogleMapsConfig googleMapsConfig;

  public OpenNotifyConfig getOpenNotifyConfig() {
    return openNotifyConfig;
  }

  public void setOpenNotifyConfig(OpenNotifyConfig openNotifyConfig) {
    this.openNotifyConfig = openNotifyConfig;
  }

  public GoogleMapsConfig getGoogleMapsConfig() {
    return googleMapsConfig;
  }

  public void setGoogleMapsConfig(GoogleMapsConfig googleMapsConfig) {
    this.googleMapsConfig = googleMapsConfig;
  }

  public LaunchLibraryConfig getLaunchLibraryConfig() {
    return launchLibraryConfig;
  }

  public void setLaunchLibraryConfig(LaunchLibraryConfig launchLibraryConfig) {
    this.launchLibraryConfig = launchLibraryConfig;
  }
}
