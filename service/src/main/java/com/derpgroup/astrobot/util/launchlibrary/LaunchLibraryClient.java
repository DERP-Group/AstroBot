package com.derpgroup.astrobot.util.launchlibrary;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.configuration.LaunchLibraryConfig;
import com.derpgroup.astrobot.util.CacheTuple;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequest;

public class LaunchLibraryClient {

  private static final Logger LOG = LoggerFactory.getLogger(LaunchLibraryClient.class);

  private static final String LAUNCHES_ENDPOINT = "/launch";
  private static final String AGENCIES_ENDPOINT = "/agency";
  
  private String launchLibraryApiRootUri;
  private String launchLibraryVersion;
  
  private ObjectMapper mapper;
  private CacheTuple<LaunchesResponse> launchesResponseCache;
  private CacheTuple<AgenciesResponse> agenciesResponseCache;
  private Map<String,Integer> agencyIdsByAbbreviation;

  private long launchesCacheTtl = 10000;
  private long agenciesCacheTtl = 10000;
  
  private int upcomingLaunchesToRetrieve;
  private int agenciesToRetrieve;
  
  //TODO: do autowiring
  //TODO: fix caching by making an extension of this class that does cache stuffs
  public LaunchLibraryClient(LaunchLibraryConfig config){
    this.launchLibraryApiRootUri = config.getLaunchLibraryApiRootUrl();
    this.launchLibraryVersion = config.getLaunchLibraryVersion();
    this.upcomingLaunchesToRetrieve = config.getUpcomingLaunchesToRetrieve();
    this.launchesCacheTtl = config.getLaunchesCacheTtl();
    this.agenciesCacheTtl = config.getAgenciesCacheTtl();
    this.agenciesToRetrieve = config.getAgenciesToRetrieve();
    
    mapper = new ObjectMapper();
    if (config.isIgnoreUnknownJsonProperties()) {
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
    launchesResponseCache = new CacheTuple<LaunchesResponse>();
    agenciesResponseCache = new CacheTuple<AgenciesResponse>();
    agencyIdsByAbbreviation = new HashMap<String,Integer>();
    
    try {
      getAgenciesWithCache();
    } catch (DerpwizardException e) {
      LOG.error("Could not pre-cache agencies.");
    }
  }
  
  public LaunchesResponse getUpcomingLaunches() throws DerpwizardException{
    HashMap<String, Object> queryParams = new HashMap<String, Object>();
    queryParams.put("next", upcomingLaunchesToRetrieve);
    queryParams.put("mode", "verbose");
    String uri = launchLibraryApiRootUri + "/" + launchLibraryVersion + LAUNCHES_ENDPOINT;
    HttpRequest request = Unirest.get(uri).queryString(queryParams);
    LOG.info("Request Url: " + request.getUrl());
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<LaunchesResponse>(){});
    }catch(JsonParseException | JsonMappingException jacksonException){
      throw new DerpwizardException("Could not parse response from LaunchLibrary");
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
    }
  }
  
  public Set<Launch> getUpcomingLaunchesByAgency(int agencyId) throws DerpwizardException{
    LaunchesResponse launchesResponse = getUpcomingLaunchesWithCache();
    Set<Launch> launchesToReturn = new LinkedHashSet<Launch>();
    for(Launch launch : launchesResponse.getLaunches()){
      if(launchMatchesAgency(launch, agencyId)){
        launchesToReturn.add(launch);
      }
    }
    return launchesToReturn;
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
  
  private AgenciesResponse getAgencies() throws DerpwizardException {
    HashMap<String, Object> queryParams = new HashMap<String, Object>();
    queryParams.put("limit", agenciesToRetrieve);
    queryParams.put("mode", "verbose");
    String uri = launchLibraryApiRootUri + "/" + launchLibraryVersion + AGENCIES_ENDPOINT;
    HttpRequest request = Unirest.get(uri).queryString(queryParams);
    LOG.info("Request Url: " + request.getUrl());
    
    try {
      HttpResponse<String> response = request.asString();
      return mapper.readValue(response.getBody(), new TypeReference<AgenciesResponse>(){});
    }catch(JsonParseException | JsonMappingException jacksonException){
      throw new DerpwizardException("Could not parse response from LaunchLibrary");
    } catch (Exception e) {
      LOG.error(e.getMessage());
      throw new DerpwizardException(e.getMessage());
    }
  }
  
  public AgenciesResponse getAgenciesWithCache() throws DerpwizardException{

    AgenciesResponse response = agenciesResponseCache.get();
    long now = System.currentTimeMillis();
    if(response == null || launchesResponseCache.getTtl() < now){
      response = getAgencies();
      agenciesResponseCache.update(response, now + agenciesCacheTtl);
      LOG.debug("Launches cache miss.");
      Map<String, Integer> agencyIdsByAbbreviation = new HashMap<String,Integer>();
      for(Agency agency : response.getAgencies()){
        agencyIdsByAbbreviation.put(agency.getAbbreviation(), agency.getId());
      }
      this.agencyIdsByAbbreviation = agencyIdsByAbbreviation;
      return response;
    }else{
      LOG.debug("Launches cache hit.");
      return response;
    }
  }

  public static boolean launchMatchesAgency(Launch launch, int agencyId){

    Agency[] rocketAgencies = launch.getRocket().getAgencies();
    for(Agency agency : rocketAgencies){
      if(agency.getId() == agencyId){
        return true;
      }
    }
    
    Pad[] pads = launch.getLocation().getPads();
    for(Pad pad : pads){
      Agency[] padAgencies = pad.getAgencies();
      for(Agency agency : padAgencies){
        if(agency.getId() == agencyId){
          return true;
        }
      }
    }
    return false;
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
  
  public int getAgencyIdByAbbreviation(String abbreviation){
    if(!agencyIdsByAbbreviation.containsKey(abbreviation)){
      return 0;
    }
    return agencyIdsByAbbreviation.get(abbreviation);
  }
}
