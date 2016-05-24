package com.derpgroup.astrobot.util.launchlibrary;

public class Pad {

  private int id;
  private String name;
  private String infoURL;
  private String wikiURL;
  private String mapURL;
  private String latitude;
  private String longitude;
  private Agency[] agencies;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInfoURL() {
    return infoURL;
  }

  public void setInfoURL(String infoURL) {
    this.infoURL = infoURL;
  }

  public String getWikiURL() {
    return wikiURL;
  }

  public void setWikiURL(String wikiURL) {
    this.wikiURL = wikiURL;
  }

  public String getMapURL() {
    return mapURL;
  }

  public void setMapURL(String mapURL) {
    this.mapURL = mapURL;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public Agency[] getAgencies() {
    return agencies;
  }

  public void setAgencies(Agency[] agencies) {
    this.agencies = agencies;
  }
}
