package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class GeonamesConfig {

  @NotNull
  private String geonamesApiRootUrl;

  @NotNull
  private String username;
  
  public String getGeonamesApiRootUrl() {
    return geonamesApiRootUrl;
  }
  
  public void setGeonamesApiRootUrl(String geonamesApiRootUrl) {
    this.geonamesApiRootUrl = geonamesApiRootUrl;
  }
  
  public String getUsername() {
    return username;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
}
