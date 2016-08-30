package com.derpgroup.astrobot.util.launchlibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public class Agency {

  private int id;
  private int type;
  private String name;
  private String abbreviation;
  private String countryCode;
  private String infoURL;
  private String wikiURL;
  private String[] infoURLs;
  
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
  
  @JsonProperty("abbrev")
  public String getAbbreviation() {
    return abbreviation;
  }

  @JsonProperty("abbrev")
  public void setAbbreviation(String abbreviation) {
    this.abbreviation = abbreviation;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
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

  public String[] getInfoURLs() {
    return infoURLs;
  }

  public void setInfoURLs(String[] infoURLs) {
    this.infoURLs = infoURLs;
  }
}
