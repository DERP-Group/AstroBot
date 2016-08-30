package com.derpgroup.astrobot.util.launchlibrary;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public class AgenciesResponse extends LaunchLibraryResponse{

  private Agency[] agencies;

  public Agency[] getAgencies() {
    return agencies;
  }

  public void setAgencies(Agency[] agencies) {
    this.agencies = agencies;
  }
}
