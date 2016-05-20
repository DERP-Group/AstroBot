package com.derpgroup.astrobot.util.opennotify;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.derpwizard.model.accountlinking.AuthenticationException;
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
  
  public OpenNotifyClient(String openNotifyApiRootUri){
    this.openNotifyApiRootUri = openNotifyApiRootUri;
    
    mapper = new ObjectMapper();
  }
  
  public AstronautsResponse getAstronauts() throws DerpwizardException{
    GetRequest request = Unirest.get(openNotifyApiRootUri + ASTRONAUTS_ENDPOINT);
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<AstronautsResponse>(){});
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
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
