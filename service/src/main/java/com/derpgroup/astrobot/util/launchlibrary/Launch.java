package com.derpgroup.astrobot.util.launchlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
@JsonIgnoreProperties(value = { "inhold", "infoURL","vidURL" })
public class Launch {

  private boolean dateLocked; //tbddate (0,1) from the API
  private boolean timeLocked; //tbdtime (0,1) from the API
  private int id;
  private int status;
  private String name;
  private String net; //Switch this to a date
  private String windowstart; //Switch this to a date
  private String windowend; //Switch this to a date
  private String isostart; //Switch this to a date
  private String isoend; //Switch this to a date
  private String isonet; //Switch this to a date
  private String wsstamp;
  private String westamp;
  private String netstamp;
  private String[] infoURLs;
  private String[] vidURLs;
  private String holdreason;
  private String failreason;
  private int probability;
  private String hashtag;
  private Location location;
  private Rocket rocket;
  private Mission[] missions;

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

  public String getWindowend() {
    return windowend;
  }

  public void setWindowend(String windowend) {
    this.windowend = windowend;
  }

  public String getIsostart() {
    return isostart;
  }

  public void setIsostart(String isostart) {
    this.isostart = isostart;
  }

  public String getIsoend() {
    return isoend;
  }

  public void setIsoend(String isoend) {
    this.isoend = isoend;
  }

  public String getIsonet() {
    return isonet;
  }

  public void setIsonet(String isonet) {
    this.isonet = isonet;
  }

  public String getWsstamp() {
    return wsstamp;
  }

  public void setWsstamp(String wsstamp) {
    this.wsstamp = wsstamp;
  }

  public String getWestamp() {
    return westamp;
  }

  public void setWestamp(String westamp) {
    this.westamp = westamp;
  }

  public String getNetstamp() {
    return netstamp;
  }

  public void setNetstamp(String netstamp) {
    this.netstamp = netstamp;
  }

  public String[] getInfoURLs() {
    return infoURLs;
  }

  public void setInfoURLs(String[] infoURLs) {
    this.infoURLs = infoURLs;
  }

  public String[] getVidURLs() {
    return vidURLs;
  }

  public void setVidURLs(String[] vidURLs) {
    this.vidURLs = vidURLs;
  }

  public String getHoldreason() {
    return holdreason;
  }

  public void setHoldreason(String holdreason) {
    this.holdreason = holdreason;
  }

  public String getFailreason() {
    return failreason;
  }

  public void setFailreason(String failreason) {
    this.failreason = failreason;
  }

  public int getProbability() {
    return probability;
  }

  public void setProbability(int probability) {
    this.probability = probability;
  }

  public String getHashtag() {
    return hashtag;
  }

  public void setHashtag(String hashtag) {
    this.hashtag = hashtag;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public Rocket getRocket() {
    return rocket;
  }

  public void setRocket(Rocket rocket) {
    this.rocket = rocket;
  }

  public Mission[] getMissions() {
    return missions;
  }

  public void setMissions(Mission[] missions) {
    this.missions = missions;
  }
}
