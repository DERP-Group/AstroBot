package com.derpgroup.astrobot.util.launchlibrary;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

public class LaunchLibraryClient {

  private static final Logger LOG = LoggerFactory.getLogger(LaunchLibraryClient.class);

  private static final String AGENCIES_ENDPOINT = "/agency";
  private static final String LAUNCHES_ENDPOINT = "/launch";
  
  private String launchLibraryApiRootUri;
  private String launchLibraryVersion;
  
  private ObjectMapper mapper;
  
  public LaunchLibraryClient(String launchLibraryApiRootUri, String launchLibraryVersion){
    this.launchLibraryApiRootUri = launchLibraryApiRootUri;
    this.launchLibraryVersion = launchLibraryVersion;
    
    mapper = new ObjectMapper();
  }
  
  public LaunchesResponse getNextLaunch() throws DerpwizardException{
    HashMap<String, Object> queryParams = new HashMap<String, Object>();
    queryParams.put("next", "1");
    String uri = launchLibraryApiRootUri + "/" + launchLibraryVersion + LAUNCHES_ENDPOINT;
    HttpRequest request = Unirest.get(uri).queryString(queryParams);
    LOG.info("Request Url: " + request.getUrl());
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<LaunchesResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
    }
  }

  public String getLaunchLibraryApiRootUri() {
    return launchLibraryApiRootUri;
  }

  public void setLaunchLibraryApiRootUri(String launchLibraryApiRootUri) {
    this.launchLibraryApiRootUri = launchLibraryApiRootUri;
  }

  public String getLaunchLibraryVersion() {
    return launchLibraryVersion;
  }

  public void setLaunchLibraryVersion(String launchLibraryVersion) {
    this.launchLibraryVersion = launchLibraryVersion;
  }
}
