package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class LaunchLibraryConfig {

  @NotNull
  private String launchLibraryApiRootUrl;
  
  private String launchLibraryVersion = "1.2";

  public String getLaunchLibraryApiRootUrl() {
    return launchLibraryApiRootUrl;
  }

  public void setLaunchLibraryApiRootUrl(String launchLibraryApiRootUrl) {
    this.launchLibraryApiRootUrl = launchLibraryApiRootUrl;
  }

  public String getLaunchLibraryVersion() {
    return launchLibraryVersion;
  }

  public void setLaunchLibraryVersion(String launchLibraryVersion) {
    this.launchLibraryVersion = launchLibraryVersion;
  }
}
