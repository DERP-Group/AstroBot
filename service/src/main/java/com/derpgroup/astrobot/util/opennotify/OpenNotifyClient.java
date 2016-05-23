package com.derpgroup.astrobot.util.opennotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.util.CacheTuple;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;

public class OpenNotifyClient {

  private static final Logger LOG = LoggerFactory.getLogger(OpenNotifyClient.class);

  private static final String ASTRONAUTS_ENDPOINT = "/astros.json";
  private static final String SPACE_STATION_LOCATION_ENDPOINT = "/iss-now.json";
  
  private String openNotifyApiRootUri;
  
  private ObjectMapper mapper;
  private CacheTuple<AstronautsResponse> astronautsResponseCache;

  private long astronautsCacheTtl = 10000;
  
  public OpenNotifyClient(String openNotifyApiRootUri){
    this.openNotifyApiRootUri = openNotifyApiRootUri;
    
    mapper = new ObjectMapper();
    astronautsResponseCache = new CacheTuple<AstronautsResponse>();
    LOG.info("Instantiated OpenNotifyClient.");
  }
  
  public OpenNotifyClient(String openNotifyApiRootUri, long astronautsCacheTtl){
    this(openNotifyApiRootUri);
    if(astronautsCacheTtl > 0){
      this.astronautsCacheTtl = astronautsCacheTtl;
    }
  }
  
  public AstronautsResponse getAstronauts() throws DerpwizardException{
    LOG.debug("Requesting list of astronauts from OpenNotify.");
    GetRequest request = Unirest.get(openNotifyApiRootUri + ASTRONAUTS_ENDPOINT);
    
    try {
      HttpResponse<String> response = request.asString();
      AstronautsResponse astronautsResponse = mapper.readValue(response.getBody(), new TypeReference<AstronautsResponse>(){});
      LOG.debug("Found list of astronauts.");
      return astronautsResponse;
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
    }
  }
  
  public AstronautsResponse getAstronautsWithCache() throws DerpwizardException{
    AstronautsResponse response = astronautsResponseCache.get();
    long now = System.currentTimeMillis();
    if(response == null || astronautsResponseCache.getTtl() < now){
      LOG.debug("Cache not up-to-date, retrieving from service.");
      response = getAstronauts();
      astronautsResponseCache.update(response, now + astronautsCacheTtl);
      return response;
    }else{
      LOG.debug("Retrieved astronauts data from cache.");
      return response;
    }
  }
  
  public SpaceStationLocationResponse getSpaceStationLocation() throws DerpwizardException{

    GetRequest request = Unirest.get(openNotifyApiRootUri + SPACE_STATION_LOCATION_ENDPOINT);
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<SpaceStationLocationResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
    }
  }

  public String getOpenNotifyApiRootUri() {
    return openNotifyApiRootUri;
  }

  public void setOpenNotifyApiRootUri(String openNotifyApiRootUri) {
    this.openNotifyApiRootUri = openNotifyApiRootUri;
  }
}
