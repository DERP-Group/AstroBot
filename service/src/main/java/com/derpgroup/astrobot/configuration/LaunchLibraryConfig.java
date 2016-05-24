package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class LaunchLibraryConfig {

  @NotNull
  private String launchLibraryApiRootUrl;
  @NotNull
  private String launchLibraryVersion = "1.2";
  private long launchesCacheTtl = 21600000;

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

  public long getLaunchesCacheTtl() {
    return launchesCacheTtl;
  }

  public void setLaunchesCacheTtl(long launchesCacheTtl) {
    this.launchesCacheTtl = launchesCacheTtl;
  }
}
