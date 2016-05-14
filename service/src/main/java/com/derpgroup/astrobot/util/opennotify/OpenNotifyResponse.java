package com.derpgroup.astrobot.util.opennotify;

public abstract class OpenNotifyResponse {

  protected String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
