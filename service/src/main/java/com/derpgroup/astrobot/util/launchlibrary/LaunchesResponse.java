package com.derpgroup.astrobot.util.launchlibrary;

public class LaunchesResponse extends LaunchLibraryResponse{
  
  private Launch[] launches;

  public Launch[] getLaunches() {
    return launches;
  }

  public void setLaunches(Launch[] launches) {
    this.launches = launches;
  }
}
