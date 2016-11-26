package com.derpgroup.astrobot.util.launchlibrary;

import com.fasterxml.jackson.annotation.JsonProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings
public class Rocket {

  private int id;
  private String name;
  private String configuration;
  private String familyName;
  private String wikiURL;
  private String imageURL;
  private String infoURL;
  private String[] infoURLs;
  private int[] imageSizes;
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

  public String getConfiguration() {
    return configuration;
  }

  public void setConfiguration(String configuration) {
    this.configuration = configuration;
  }

  @JsonProperty("familyname")
  public String getFamilyName() {
    return familyName;
  }

  @JsonProperty("familyname")
  public void setFamilyName(String familyName) {
    this.familyName = familyName;
  }

  public String getImageURL() {
    return imageURL;
  }

  public void setImageURL(String imageURL) {
    this.imageURL = imageURL;
  }

  public int[] getImageSizes() {
    return imageSizes;
  }

  public void setImageSizes(int[] imageSizes) {
    this.imageSizes = imageSizes;
  }

  public Agency[] getAgencies() {
    return agencies;
  }

  public void setAgencies(Agency[] agencies) {
    this.agencies = agencies;
  }

  public String getWikiURL() {
    return wikiURL;
  }

  public void setWikiURL(String wikiURL) {
    this.wikiURL = wikiURL;
  }

  public String getInfoURL() {
    return infoURL;
  }

  public void setInfoURL(String infoURL) {
    this.infoURL = infoURL;
  }

  public String[] getInfoURLs() {
    return infoURLs;
  }

  public void setInfoURLs(String[] infoURLs) {
    this.infoURLs = infoURLs;
  }
}
