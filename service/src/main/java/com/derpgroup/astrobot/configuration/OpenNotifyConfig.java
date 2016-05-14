package com.derpgroup.astrobot.configuration;

import javax.validation.constraints.NotNull;

public class OpenNotifyConfig {

  @NotNull
  private String openNotifyApiRootUrl;

  public String getOpenNotifyApiRootUrl() {
    return openNotifyApiRootUrl;
  }

  public void setOpenNotifyApiRootUrl(String openNotifyApiRootUrl) {
    this.openNotifyApiRootUrl = openNotifyApiRootUrl;
  }
}
