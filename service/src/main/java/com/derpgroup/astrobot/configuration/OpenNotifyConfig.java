package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class OpenNotifyConfig {

  @NotNull
  private String openNotifyApiRootUrl;
  private long astronautsCacheTtl;

  public String getOpenNotifyApiRootUrl() {
    return openNotifyApiRootUrl;
  }

  public void setOpenNotifyApiRootUrl(String openNotifyApiRootUrl) {
    this.openNotifyApiRootUrl = openNotifyApiRootUrl;
  }

  public long getAstronautsCacheTtl() {
    return astronautsCacheTtl;
  }

  public void setAstronautsCacheTtl(long astronautsCacheTtl) {
    this.astronautsCacheTtl = astronautsCacheTtl;
  }
}
