package com.derpgroup.astrobot.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AstrobotConfig {

  @Valid
  @NotNull
  private OpenNotifyConfig openNotifyConfig;

  public OpenNotifyConfig getOpenNotifyConfig() {
    return openNotifyConfig;
  }

  public void setOpenNotifyConfig(OpenNotifyConfig openNotifyConfig) {
    this.openNotifyConfig = openNotifyConfig;
  }
}
