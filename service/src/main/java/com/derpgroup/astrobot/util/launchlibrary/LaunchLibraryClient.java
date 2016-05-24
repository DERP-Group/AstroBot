package com.derpgroup.astrobot.util.launchlibrary;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.util.CacheTuple;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

public class LaunchLibraryClient {

  private static final Logger LOG = LoggerFactory.getLogger(LaunchLibraryClient.class);

  private static final String LAUNCHES_ENDPOINT = "/launch";
  
  private String launchLibraryApiRootUri;
  private String launchLibraryVersion;
  
  private ObjectMapper mapper;
  private CacheTuple<LaunchesResponse> launchesResponseCache;

  private long launchesCacheTtl = 10000;
  
  public LaunchLibraryClient(String launchLibraryApiRootUri, String launchLibraryVersion, long launchesCacheTtl){
    this.launchLibraryApiRootUri = launchLibraryApiRootUri;
    this.launchLibraryVersion = launchLibraryVersion;
    this.launchesCacheTtl = launchesCacheTtl;
    
    mapper = new ObjectMapper();
    launchesResponseCache = new CacheTuple<LaunchesResponse>();
  }
  
  public LaunchesResponse getUpcomingLaunches() throws DerpwizardException{
    HashMap<String, Object> queryParams = new HashMap<String, Object>();
    queryParams.put("next", "10");
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
  
  public LaunchesResponse getUpcomingLaunchesWithCache() throws DerpwizardException{

    LaunchesResponse response = launchesResponseCache.get();
    long now = System.currentTimeMillis();
    if(response == null || launchesResponseCache.getTtl() < now){
      response = getUpcomingLaunches();
      launchesResponseCache.update(response, now + launchesCacheTtl);
      LOG.debug("Launches cache miss.");
      return response;
    }else{
      LOG.debug("Launches cache hit.");
      return response;
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
