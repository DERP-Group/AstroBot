package com.derpgroup.astrobot.util.launchlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(value = { "inhold" })
public class Launch {

  private boolean dateLocked; //tbddate (0,1) from the API
  private boolean timeLocked; //tbdtime (0,1) from the API
  private int id;
  private int status;
  private String name;
  private String net; //Switch this to a date
  private String windowstart; //Switch this to a date

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @JsonProperty("tbddate")
  public int getDateLocked() {
    return dateLocked ? 0 : 1;
  }

  @JsonProperty("tbddate")
  public void setDateLocked(int tbd) {
    this.dateLocked = (tbd == 0);
  }
  
  public boolean isDateLocked() {
    return dateLocked;
  }

  public void setDateLocked(boolean dateLocked) {
    this.dateLocked = dateLocked;
  }

  @JsonProperty("tbdtime")
  public int getTimeLocked() {
    return timeLocked ? 0 : 1;
  }

  @JsonProperty("tbdtime")
  public void setTimeLocked(int tbd) {
    this.timeLocked = (tbd == 0);
  }
  
  public boolean isTimeLocked() {
    return timeLocked;
  }

  public void setTimeLocked(boolean timeLocked) {
    this.timeLocked = timeLocked;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getNet() {
    return net;
  }

  public void setNet(String net) {
    this.net = net;
  }

  public String getWindowstart() {
    return windowstart;
  }

  public void setWindowstart(String windowstart) {
    this.windowstart = windowstart;
  }
}
