/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.derpgroup.astrobot.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.AstroBotMetadata;
import com.derpgroup.astrobot.MixInModule;
import com.derpgroup.astrobot.configuration.AstroBotConfig;
import com.derpgroup.astrobot.configuration.GeonamesConfig;
import com.derpgroup.astrobot.configuration.LaunchLibraryConfig;
import com.derpgroup.astrobot.configuration.MainConfig;
import com.derpgroup.astrobot.configuration.OpenNotifyConfig;
import com.derpgroup.astrobot.util.geonames.GeonamesClient;
import com.derpgroup.astrobot.util.geonames.OceansResponse;
import com.derpgroup.astrobot.util.launchlibrary.Launch;
import com.derpgroup.astrobot.util.launchlibrary.LaunchLibraryClient;
import com.derpgroup.astrobot.util.launchlibrary.LaunchesResponse;
import com.derpgroup.astrobot.util.opennotify.Astronaut;
import com.derpgroup.astrobot.util.opennotify.AstronautsResponse;
import com.derpgroup.astrobot.util.opennotify.OpenNotifyClient;
import com.derpgroup.astrobot.util.opennotify.SpaceStationLocationResponse;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.derpgroup.derpwizard.voice.model.ConversationHistoryEntry;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

/**
 * Manager class for dispatching input messages.
 *
 * @author David
 * @author Eric
 * @since 0.0.1
 */
public class AstroBotManager {
  private static final Logger LOG = LoggerFactory.getLogger(AstroBotManager.class);
  
  private static final HashSet<String> metaSubjects;
  private static final HashMap<String, String> canonicalAgencyAbbreviations;
  private static final ArrayList<String> menuQueryQuips;
  
  static{
    metaSubjects = new HashSet<String>();
    metaSubjects.add("REPEAT");
    metaSubjects.add("YES");
    metaSubjects.add("NO");
    metaSubjects.add("NEXT");
    metaSubjects.add("PREVIOUS");
  }
  
  static{
    canonicalAgencyAbbreviations = new HashMap<String,String>();
    canonicalAgencyAbbreviations.put("spacex", "SpX");
    canonicalAgencyAbbreviations.put("spaceexplorationtechnologies","SpX");
    canonicalAgencyAbbreviations.put("orbital", "OA");
    canonicalAgencyAbbreviations.put("orbitalatk", "OA");
    canonicalAgencyAbbreviations.put("orbitalsciencescorporation", "OA");
    canonicalAgencyAbbreviations.put("allianttechsystems", "OA");
    canonicalAgencyAbbreviations.put("ula", "ULA");
    canonicalAgencyAbbreviations.put("unitedlaunchalliance", "ULA");
    canonicalAgencyAbbreviations.put("arianespace", "ASA");
    canonicalAgencyAbbreviations.put("jaxa", "JAXA");
    canonicalAgencyAbbreviations.put("japanaerospaceexplorationagency", "JAXA");
    canonicalAgencyAbbreviations.put("russianfederalspaceagency", "FKA");
    canonicalAgencyAbbreviations.put("roscosmos", "FKA");
    canonicalAgencyAbbreviations.put("nationalaeronauticsandspaceadministration", "NASA");
    canonicalAgencyAbbreviations.put("nasa", "NASA");
  }
  
  static{
    menuQueryQuips = new ArrayList<String>();
    menuQueryQuips.add("What can I do for you?");
    menuQueryQuips.add("What can I help you with?");
    menuQueryQuips.add("What would you like to know?");
    menuQueryQuips.add("What do you want to explore?");
  }

  static {
    ConversationHistoryUtils.getMapper().registerModule(new MixInModule());
  }
  
  private OpenNotifyClient openNotifyClient;
  private LaunchLibraryClient launchLibraryClient;
  private GeoApiContext googleMapsGeoApiContext;
  private GeonamesClient geonamesClient;
  private ExecutorService executor;
  private long responseTimeout = 4500;
  
  public AstroBotManager(MainConfig config) {
    AstroBotConfig astroBotConfig = config.getAstroBotConfig();
    
    OpenNotifyConfig openNotifyConfig = astroBotConfig.getOpenNotifyConfig();
    String openNotifyApiRootUrl = openNotifyConfig.getOpenNotifyApiRootUrl();
    long astronautsCacheTtl = openNotifyConfig.getAstronautsCacheTtl();
    openNotifyClient = new OpenNotifyClient(openNotifyApiRootUrl, astronautsCacheTtl);
    String googleMapsApiKey = astroBotConfig.getGoogleMapsConfig().getApiKey();
    googleMapsGeoApiContext = new GeoApiContext().setApiKey(googleMapsApiKey);
    
    LaunchLibraryConfig launchLibraryConfig = astroBotConfig.getLaunchLibraryConfig();
    launchLibraryClient = new LaunchLibraryClient(launchLibraryConfig);
    
    GeonamesConfig geonamesConfig = astroBotConfig.getGeonamesConfig();
    geonamesClient = new GeonamesClient(geonamesConfig);
    
    executor = Executors.newSingleThreadExecutor();
  }

  public void handleRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException{
    LOG.debug("Doing request of type '" + serviceInput.getSubject() + "'");
    switch(serviceInput.getSubject()){
    case "PEOPLE_IN_SPACE":
      doPeopleInSpaceRequest(serviceInput, serviceOutput);
      break;
    case "WHO_IS_IN_SPACE":
      doWhoIsInSpaceRequest(serviceInput, serviceOutput);
      break;
    case "INTERNATIONAL_SPACE_STATION":
      doInternationalSpaceStationRequest(serviceInput, serviceOutput);
      break;
    case "NEXT_LAUNCH":
      doNextLaunchRequest(serviceInput, serviceOutput);
      break;
    case "NEXT_LAUNCH_FOR_AGENCY":
      doNextLaunchByAgencyRequest(serviceInput, serviceOutput);
      break;
    case "HELP":
      doHelpRequest(serviceInput, serviceOutput);
      break;
    case "START_OF_CONVERSATION":
      doHelloRequest(serviceInput, serviceOutput);
      break;
    case "END_OF_CONVERSATION":
      doGoodbyeRequest(serviceInput, serviceOutput);
      break;
    case "CANCEL":
      doCancelRequest(serviceInput, serviceOutput);
      break;
    case "STOP":
      doStopRequest(serviceInput, serviceOutput);
      break;
    case "REPEAT":
      doRepeatRequest(serviceInput, serviceOutput);
      break;
    case "YES":
      doYesRequest(serviceInput, serviceOutput);
      break;
    case "NO":
      doNoRequest(serviceInput, serviceOutput);
      break;
    case "NEXT":
      doNextRequest(serviceInput, serviceOutput);
      break;
    case "PREVIOUS":
      doPreviousRequest(serviceInput, serviceOutput);
      break;
    default:
      throw new DerpwizardException("Unrecognized request subject '" + serviceInput.getSubject() + "'.");
    }
  }

  private void doPeopleInSpaceRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException{
    
    AstronautsResponse astronautsResponse;
    try {
      astronautsResponse = getAstronautsViaFuture(responseTimeout);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      buildTimeoutError(serviceOutput, "the people in space");
      return;
    }
    serviceOutput.getVisualOutput().setTitle("Number of People in Space");
    serviceOutput.getVisualOutput().setText("There are currently " + astronautsResponse.getNumber() + " people in space.");
    serviceOutput.getVoiceOutput().setSsmltext("There are currently " + astronautsResponse.getNumber() + " people in space. Would you like to hear their names and ships?");
    LOG.debug("Built service output for 'PEOPLE_IN_SPACE' request.");
  }

  private void doWhoIsInSpaceRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    AstronautsResponse astronautsResponse;
    try {
      astronautsResponse = getAstronautsViaFuture(responseTimeout);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      buildTimeoutError(serviceOutput, "the people in space");
      return;
    }
    
    serviceOutput.getVisualOutput().setTitle("People in Space");

    StringBuilder visualTextBuilder = new StringBuilder("The people currently in space are:\n");
    StringBuilder voiceSsmlTextBuilder = new StringBuilder("The people currently in space are<break time=\"800ms\" />");
    
    HashSet<String> inhabitedSpacecraft = new HashSet<String>();
    for(Astronaut astronaut : astronautsResponse.getPeople()){
      inhabitedSpacecraft.add(astronaut.getCraft());
    }
    
    for(Astronaut astronaut : astronautsResponse.getPeople()){
      
      visualTextBuilder.append("\n" + astronaut.getName() + ",");
      voiceSsmlTextBuilder.append(astronaut.getName() + ",");
      if(inhabitedSpacecraft.size() > 1){
        visualTextBuilder.append(" on " + astronaut.getCraft());
        visualTextBuilder.append(" on " + astronaut.getCraft());
      }
    }
    
    if(inhabitedSpacecraft.size() == 1){
      String craftName = inhabitedSpacecraft.toArray()[0].toString();
      visualTextBuilder.append("\n\nThey are all on " + craftName);
      voiceSsmlTextBuilder.append("<break />They are all on " + craftName);
    }
    
    serviceOutput.getVisualOutput().setText(visualTextBuilder.toString());
    serviceOutput.getVoiceOutput().setSsmltext(voiceSsmlTextBuilder.toString());
  }

  private void doInternationalSpaceStationRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    long timeRemaining = responseTimeout;
    long prefetch = 0;
    long postfetch = 0;
    SpaceStationLocationResponse spaceStationResponse;
    try {
      prefetch = System.currentTimeMillis();
      spaceStationResponse = getSpaceStationLocationViaFuture(timeRemaining);
      postfetch = System.currentTimeMillis();
      timeRemaining -= (postfetch - prefetch);
      LOG.info("Time remaining: " + timeRemaining);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      buildTimeoutError(serviceOutput, "the ISS's location");
      return;
    }
    
    double latitude = spaceStationResponse.getSpaceStationLocation().getLatitude();
    double longitude = spaceStationResponse.getSpaceStationLocation().getLongitude();
    //This stuff should probably be pulled out into its own place
    LatLng coordinates = new LatLng(latitude,
        longitude);
    GeocodingApiRequest req = GeocodingApi.reverseGeocode(googleMapsGeoApiContext, coordinates);
    String countryName = null;
    String administrativeAreaName = null;
    GeocodingResult[] results;
    try {
      prefetch = System.currentTimeMillis();
      results = getReverseGeocodeViaFuture(req, timeRemaining);
      postfetch = System.currentTimeMillis();
      timeRemaining -= (postfetch - prefetch);
      LOG.info("Time remaining: " + timeRemaining);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      buildTimeoutError(serviceOutput, "the ISS's location");
      return;
    }
    if(results != null && results.length >= 1){
      GeocodingResult result = results[0];
      AddressComponent[] addressComponents = result.addressComponents;
      for(AddressComponent addressComponent : addressComponents){
        AddressComponentType[] addressComponentTypes = addressComponent.types;
        for(AddressComponentType addressComponentType : addressComponentTypes){
          if(addressComponentType.name().equalsIgnoreCase("country")){
            countryName = addressComponent.longName;
          }
          if(addressComponentType.name().equalsIgnoreCase("administrative_area_level_1")){
            administrativeAreaName = addressComponent.longName;
          }
        }
      }
    }

    StringBuilder locationName = new StringBuilder();
    if(countryName == null){
      OceansResponse oceansResponse;
      try {
        prefetch = System.currentTimeMillis();
        oceansResponse = getGeonameViaFuture(latitude, longitude, timeRemaining);
        postfetch = System.currentTimeMillis();
        timeRemaining -= (postfetch - prefetch);
        LOG.info("Time remaining: " + timeRemaining);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        buildTimeoutError(serviceOutput, "the ISS's location");
        return;
      }
      if(oceansResponse == null || oceansResponse.getOcean() == null || StringUtils.isEmpty(oceansResponse.getOcean().getName())){
        locationName.append("international waters");
        LOG.error("Could not resolve lat: " + latitude + ", lng: " + longitude +" over land or sea.");
      }else{
        locationName.append("the " + oceansResponse.getOcean().getName());
      }
    }else{
      if(administrativeAreaName != null){
        locationName.append(administrativeAreaName);
        locationName.append(", ");
      }else{
        LOG.info("Could not resolve administrativeName for area in '" + countryName + "'.");        
      }
      locationName.append(countryName);
    }
    LOG.info("CountryName: " + countryName);
    LOG.info("Administrative Area: " + administrativeAreaName);
    serviceOutput.getVisualOutput().setTitle("Where is the ISS?");
    serviceOutput.getVisualOutput().setText("The International Space Station is currently over "
        + locationName + ".\n\n Coordinates: \n "
        + spaceStationResponse.getSpaceStationLocation().getLatitude() + ","
        + spaceStationResponse.getSpaceStationLocation().getLongitude());
    serviceOutput.getVoiceOutput().setSsmltext("The ISS is currently over "
        + locationName + ".");
  }

  private void doNextLaunchRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    doIndexedUpcomingLaunchRequest(serviceInput, serviceOutput, 0);
  }

  private void doNextLaunchByAgencyRequest(ServiceInput serviceInput,ServiceOutput serviceOutput) throws DerpwizardException {
    if(MapUtils.isEmpty(serviceInput.getMessageAsMap()) || !serviceInput.getMessageAsMap().containsKey("agencyName")){
      LOG.error("Launch by agency method triggered, but no agencyName was found. Defaulting to agency-agnostic request.");
      doNextLaunchRequest(serviceInput, serviceOutput);
    }
    String agencyName = serviceInput.getMessageAsMap().get("agencyName");
    doIndexedUpcomingLaunchRequestByAgencyId(serviceInput, serviceOutput, 0, agencyName);
  }
  
  private void doIndexedUpcomingLaunchRequest(ServiceInput serviceInput, ServiceOutput serviceOutput, int index) throws DerpwizardException {
    doIndexedUpcomingLaunchRequestByAgencyId(serviceInput, serviceOutput, index, null);
  }
  
  private void doIndexedUpcomingLaunchRequestByAgencyId(ServiceInput serviceInput, ServiceOutput serviceOutput, int index, String agencyName) throws DerpwizardException {
    Launch[] launches = new Launch[0];
    String agencyNameOutputModifier = "";
    if(agencyName == null){
      LaunchesResponse launchesResponse = launchLibraryClient.getUpcomingLaunchesWithCache();
      launches = launchesResponse.getLaunches();
    }else{
      agencyNameOutputModifier = " for " + agencyName;
      String canonicalAgencyAbbreviation = canonicalAgencyAbbreviations.get(agencyName);
      int agencyId = launchLibraryClient.getAgencyIdByAbbreviation(canonicalAgencyAbbreviation);
      Set<Launch> launchesResponse = launchLibraryClient.getUpcomingLaunchesByAgency(agencyId);
      launches = launchesResponse.toArray(launches);
    }
    if(launches.length < 1){
      throw new DerpwizardException("Houston, we have a problem.  I could not find any upcoming launches to tell you about.");
    }else if(index < 0 || index > launches.length){
      throw new DerpwizardException("Looks like we've reached the end of the line - no more launches to talk about in that direction.");
    }
    Launch launch = launches[index];
    String launchDateString = launch.getIsostart(); //Would prefer to use timestamps here, but they aren't always provided
    DateTime launchDateTime = DateTime.parse(launchDateString, DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss'Z'").withZoneUTC());
    serviceOutput.getVisualOutput().setTitle("Next Launch:");
    
    String[] launchStringComponents = launch.getName().split("\\|");
    
    
    StringBuilder outputStringBuilder = new StringBuilder();
    if(index == 0){
      outputStringBuilder.append("The next launch" + agencyNameOutputModifier + " will be a ");
    }else{
      outputStringBuilder.append("The following launch" + agencyNameOutputModifier + " will be a ");
    }
    outputStringBuilder.append(launchStringComponents[0]);
    outputStringBuilder.append(". ");
    if(launchStringComponents.length > 0){
      outputStringBuilder.append("It will be launching ");
      outputStringBuilder.append(launchStringComponents[1]);
    }
    if(launch.isDateLocked()){
      outputStringBuilder.append(", on ");
    }else{
      outputStringBuilder.append(", targetted for ");
    }
    outputStringBuilder.append(launchDateTime.toString(DateTimeFormat.forPattern("MMMM dd',' yyyy")));
    if(launch.isTimeLocked()){
      outputStringBuilder.append(" at" + launchDateTime.toString(DateTimeFormat.forPattern(" hh:MM a")) + " Universal Time");
    }
    outputStringBuilder.append(". To hear about another launch, say \"next\".");
    String outputString = outputStringBuilder.toString();
    outputString = outputString.replace("&", "and");
    serviceOutput.getVisualOutput().setText(outputString);
    serviceOutput.getVoiceOutput().setSsmltext(outputString);
    AstroBotMetadata metadata = (AstroBotMetadata)serviceOutput.getMetadata();
    metadata.setUpcomingLaunchesIndex(index);
  }

  protected void doHelpRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVisualOutput().setTitle("Help");
    serviceOutput.getVisualOutput().setText("Topics:\n How many people are in space?\n Where is the ISS\n When is the next launch?");
    serviceOutput.getVoiceOutput().setSsmltext("You can ask one of the following questions<break /> how many people are in space?<break /> where is the international space station?<break /> when is the next launch?<break /> You can also ask for launches for a specific space agency like NASA or SpaceX.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doHelloRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    doMenuQuery(serviceInput, serviceOutput);
  }

  protected void doGoodbyeRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("AstroBot over and out.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doCancelRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Copy that Houston.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doStopRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Roger that.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doRepeatRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.setConversationEnded(true);
  }

  protected void doYesRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), metaSubjects);
    if(entry.getMessageSubject().equalsIgnoreCase("PEOPLE_IN_SPACE")){
      doWhoIsInSpaceRequest(serviceInput, serviceOutput);
    }else{
      serviceOutput.getVisualOutput().setTitle("Unexpected input.");
      serviceOutput.getVisualOutput().setText("I received input that seems like an affirmative answer, but I don't recall asking a question.");
      serviceOutput.getVoiceOutput().setSsmltext("I received input that seems like an affirmative answer, but I don't recall asking a question.");
      serviceOutput.setConversationEnded(true);
    }
  }

  protected void doNoRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), metaSubjects);
    if(entry.getMessageSubject().equalsIgnoreCase("PEOPLE_IN_SPACE")){
      doMenuQuery(serviceInput, serviceOutput);
    }else{
      doStopRequest(serviceInput, serviceOutput);
    }
  }

  private void doNextRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), metaSubjects);
    
    if(entry.getMessageSubject().equalsIgnoreCase("NEXT_LAUNCH")){
      int index = ((AstroBotMetadata)serviceInput.getMetadata()).getUpcomingLaunchesIndex();
      doIndexedUpcomingLaunchRequest(serviceInput, serviceOutput, index + 1);
    }else if(entry.getMessageSubject().equalsIgnoreCase("NEXT_LAUNCH_FOR_AGENCY")){
      String agencyName = entry.getMessageMap().get("agencyName");
      if(StringUtils.isEmpty(agencyName)){
        throw new DerpwizardException("Couldn't find an agency name on which to lookup upcoming launches.");
      }
      int index = ((AstroBotMetadata)serviceInput.getMetadata()).getUpcomingLaunchesIndex();
      doIndexedUpcomingLaunchRequestByAgencyId(serviceInput, serviceOutput, index + 1, agencyName);
    }else{
      serviceOutput.getVisualOutput().setTitle("Unexpected input.");
      serviceOutput.getVisualOutput().setText("I received input that seems like you want the next entry, but I don't know for what.");
      serviceOutput.getVoiceOutput().setSsmltext("I received input that seems like you want the next entry, but I don't know for what.");
      serviceOutput.setConversationEnded(true);
    }
  }

  private void doPreviousRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), metaSubjects);
    
    if(entry.getMessageSubject().equalsIgnoreCase("NEXT_LAUNCH")){
      int index = ((AstroBotMetadata)serviceInput.getMetadata()).getUpcomingLaunchesIndex();
      doIndexedUpcomingLaunchRequest(serviceInput, serviceOutput, index - 1);
    }else if(entry.getMessageSubject().equalsIgnoreCase("NEXT_LAUNCH_FOR_AGENCY")){
      String agencyName = entry.getMessageMap().get("agencyName");
      if(StringUtils.isEmpty(agencyName)){
        throw new DerpwizardException("Couldn't find an agency name on which to lookup upcoming launches.");
      }
      int index = ((AstroBotMetadata)serviceInput.getMetadata()).getUpcomingLaunchesIndex();
      doIndexedUpcomingLaunchRequestByAgencyId(serviceInput, serviceOutput, index - 1, agencyName);
    }else{
      serviceOutput.getVisualOutput().setTitle("Unexpected input.");
      serviceOutput.getVisualOutput().setText("I received input that seems like you want the previous entry, but I don't know for what.");
      serviceOutput.getVoiceOutput().setSsmltext("I received input that seems like you want the previous entry, but I don't know for what.");
      serviceOutput.setConversationEnded(true);
    }
  }
  
  private void doMenuQuery(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {

    StringBuilder visualTextBuilder = new StringBuilder();
    StringBuilder audioTextBuilder = new StringBuilder();
    StringBuilder delayedAudioTextBuilder = new StringBuilder();

    if(serviceInput.getSubject().equalsIgnoreCase("START_OF_CONVERSATION")){
      visualTextBuilder.append("Greetings, Earthling. ");
      audioTextBuilder.append("Greetings Earthling. ");
    }else if(serviceInput.getSubject().equalsIgnoreCase("NO")){
      audioTextBuilder.append("Understood. Aside from that, ");
    }
    
    String quip = menuQueryQuips.get(RandomUtils.nextInt(menuQueryQuips.size()));
    visualTextBuilder.append(quip);
    audioTextBuilder.append(quip);
    delayedAudioTextBuilder.append("You can say <break />help<break /> if you need some suggestions for commands.");

    if(serviceInput.getSubject().equalsIgnoreCase("NO")){
      delayedAudioTextBuilder.append(" Or you can say <break />exit<break /> to take the escape pod out of Astro Bot.");
    }
    
    serviceOutput.getVisualOutput().setTitle("Blast off with AstroBot!");
    serviceOutput.getVisualOutput().setText(visualTextBuilder.toString());
    serviceOutput.getVoiceOutput().setSsmltext(audioTextBuilder.toString());
    serviceOutput.getDelayedVoiceOutput().setSsmltext(delayedAudioTextBuilder.toString());
    serviceOutput.setConversationEnded(false);
  }

  private void buildTimeoutError(ServiceOutput serviceOutput, String timeoutSubject) {
    LOG.error("Timed out looking for: " + timeoutSubject);

    String message = "Houston, we have a problem. I couldn't find " + timeoutSubject + " because one of my sources was too slow to respond.";
    serviceOutput.getVisualOutput().setTitle("Timeout in downstream service");
    serviceOutput.getVisualOutput().setText(message);
    serviceOutput.getVoiceOutput().setSsmltext(message);
  }

  private AstronautsResponse getAstronautsViaFuture(long responseTimeout) throws InterruptedException, ExecutionException, TimeoutException {
    Future<AstronautsResponse> response = executor.submit(new Callable<AstronautsResponse>() {
        @Override
        public AstronautsResponse call() throws Exception {
          return openNotifyClient.getAstronautsWithCache();
        }
    });
    return response.get(responseTimeout, TimeUnit.MILLISECONDS);
  }

  private SpaceStationLocationResponse getSpaceStationLocationViaFuture(long responseTimeout) throws InterruptedException, ExecutionException, TimeoutException {
    Future<SpaceStationLocationResponse> response = executor.submit(new Callable<SpaceStationLocationResponse>() {
        @Override
        public SpaceStationLocationResponse call() throws Exception {
          return openNotifyClient.getSpaceStationLocation();
        }
    });
    return response.get(responseTimeout, TimeUnit.MILLISECONDS);
  }

  private GeocodingResult[] getReverseGeocodeViaFuture(GeocodingApiRequest req, long responseTimeout) throws InterruptedException, ExecutionException, TimeoutException {
    Future<GeocodingResult[]> response = executor.submit(new Callable<GeocodingResult[]>() {
        @Override
        public GeocodingResult[] call() throws Exception {
          return req.await();
        }
    });
    return response.get(responseTimeout, TimeUnit.MILLISECONDS);
  }

  private OceansResponse getGeonameViaFuture(double latitude, double longitude, long responseTimeout) throws InterruptedException, ExecutionException, TimeoutException {
    Future<OceansResponse> response = executor.submit(new Callable<OceansResponse>() {
        @Override
        public OceansResponse call() throws Exception {
          return geonamesClient.getOceanNameByCoordinates(latitude, longitude);
        }
    });
    return response.get(responseTimeout, TimeUnit.MILLISECONDS);
  }
}
