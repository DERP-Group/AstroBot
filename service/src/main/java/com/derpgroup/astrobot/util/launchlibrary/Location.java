package com.derpgroup.astrobot.util.launchlibrary;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public class Location {

  private int id;
  private String name;
  private String infoURL;
  private String wikiURL;
  private String countryCode;
  private Pad[] pads;

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

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public Pad[] getPads() {
    return pads;
  }

  public void setPads(Pad[] pads) {
    this.pads = pads;
  }
}
