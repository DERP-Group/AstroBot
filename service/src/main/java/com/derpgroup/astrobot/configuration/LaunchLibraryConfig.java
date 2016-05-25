package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class LaunchLibraryConfig {

  @NotNull
  private String launchLibraryApiRootUrl;
  private String launchLibraryVersion = "1.2";
  private int upcomingLaunchesToRetrieve = 100;
  private int agenciesToRetrieve = 500;
  private long launchesCacheTtl = 21600000;
  private long agenciesCacheTtl = 604800000;

  public String getLaunchLibraryApiRootUrl() {
    return launchLibraryApiRootUrl;
  }

  public void setLaunchLibraryApiRootUrl(String launchLibraryApiRootUrl) {
    this.launchLibraryApiRootUrl = launchLibraryApiRootUrl;
  }

  public String getLaunchLibraryVersion() {
    return launchLibraryVersion;
  }

  public int getUpcomingLaunchesToRetrieve() {
    return upcomingLaunchesToRetrieve;
  }

  public void setUpcomingLaunchesToRetrieve(int upcomingLaunchesToRetrieve) {
    this.upcomingLaunchesToRetrieve = upcomingLaunchesToRetrieve;
  }

  public void setLaunchLibraryVersion(String launchLibraryVersion) {
    this.launchLibraryVersion = launchLibraryVersion;
  }

  public int getAgenciesToRetrieve() {
    return agenciesToRetrieve;
  }

  public void setAgenciesToRetrieve(int agenciesToRetrieve) {
    this.agenciesToRetrieve = agenciesToRetrieve;
  }

  public long getLaunchesCacheTtl() {
    return launchesCacheTtl;
  }

  public void setLaunchesCacheTtl(long launchesCacheTtl) {
    this.launchesCacheTtl = launchesCacheTtl;
  }

  public long getAgenciesCacheTtl() {
    return agenciesCacheTtl;
  }

  public void setAgenciesCacheTtl(long agenciesCacheTtl) {
    this.agenciesCacheTtl = agenciesCacheTtl;
  }
}
