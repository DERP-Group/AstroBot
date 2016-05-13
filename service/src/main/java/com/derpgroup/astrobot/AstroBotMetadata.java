package com.derpgroup.astrobot;

import java.util.List;
import java.util.Map;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;


@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type", defaultImpl = AstroBotMetadata.class)
public class AstroBotMetadata extends CommonMetadata {
  
  private Map<Integer,List<Integer>> previousRolls;

  public Map<Integer, List<Integer>> getPreviousRolls() {
    return previousRolls;
  }

  public void setPreviousRolls(Map<Integer, List<Integer>> previousRolls) {
    this.previousRolls = previousRolls;
  }
}
