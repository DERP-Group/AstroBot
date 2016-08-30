package com.derpgroup.astrobot.util.geonames;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.configuration.GeonamesConfig;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

public class GeonamesClient {
  
  private static final Logger LOG = LoggerFactory.getLogger(GeonamesClient.class);

  private static final String OCEANS_ENDPOINT = "/oceanJSON";
  
  private String username;  
  private ObjectMapper mapper;
  
  private String geonamesApiRootUri;
  
  public GeonamesClient(GeonamesConfig config){
    username = config.getUsername();
    geonamesApiRootUri = config.getGeonamesApiRootUrl();
    mapper = new ObjectMapper();
  }
  
  public OceansResponse getOceanNameByCoordinates(double latitude, double longitude) throws DerpwizardException{
    HashMap<String, Object> queryParams = new HashMap<String, Object>();
    queryParams.put("username", username);
    queryParams.put("lat", latitude);
    queryParams.put("lng", longitude);
    String uri = geonamesApiRootUri + OCEANS_ENDPOINT;
    HttpRequest request = Unirest.get(uri).queryString(queryParams);
    LOG.info("Request Url: " + request.getUrl());
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<OceansResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
    }
  }
}
