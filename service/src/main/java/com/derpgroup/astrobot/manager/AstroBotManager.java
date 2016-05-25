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

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.AstroBotMetadata;
import com.derpgroup.astrobot.MixInModule;
import com.derpgroup.astrobot.configuration.AstroBotConfig;
import com.derpgroup.astrobot.configuration.LaunchLibraryConfig;
import com.derpgroup.astrobot.configuration.MainConfig;
import com.derpgroup.astrobot.configuration.OpenNotifyConfig;
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

  static {
    ConversationHistoryUtils.getMapper().registerModule(new MixInModule());
  }
  
  private OpenNotifyClient openNotifyClient;
  private LaunchLibraryClient launchLibraryClient;
  private GeoApiContext googleMapsGeoApiContext;
  
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
    AstronautsResponse astronautsResponse = openNotifyClient.getAstronautsWithCache();
    serviceOutput.getVisualOutput().setTitle("Number of People in Space");
    serviceOutput.getVisualOutput().setText("There are currently " + astronautsResponse.getNumber() + " people in space.");
    serviceOutput.getVoiceOutput().setSsmltext("There are currently " + astronautsResponse.getNumber() + " people in space. Would you like to hear their names and ships?");
    LOG.debug("Built service output for 'PEOPLE_IN_SPACE' request.");
  }

  private void doWhoIsInSpaceRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    AstronautsResponse astronautsResponse = openNotifyClient.getAstronautsWithCache();
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
    SpaceStationLocationResponse astronautsResponse = openNotifyClient.getSpaceStationLocation();

    //This stuff should probably be pulled out into its own place
    LatLng coordinates = new LatLng(astronautsResponse.getSpaceStationLocation().getLatitude(),
        astronautsResponse.getSpaceStationLocation().getLongitude());
    GeocodingApiRequest req = GeocodingApi.reverseGeocode(googleMapsGeoApiContext, coordinates);
    String countryName = null;
    String administrativeAreaName = null;
    try {
      GeocodingResult[] results = req.await();
      if(results == null || results.length < 1){
        //It's probably over the ocean - do special handling here
        LOG.error("No results");
      }else{
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
    } catch (Exception e) {
      throw new DerpwizardException("Could not lookup location of ISS from known coordinates.");
    }

    String locationName;
    if(countryName == null){
      locationName = "international waters";
    }else{
      locationName = administrativeAreaName + ", " + countryName;
    }
    LOG.info("CountryName: " + countryName);
    LOG.info("Administrative Area: " + administrativeAreaName);
    serviceOutput.getVisualOutput().setTitle("Where is the ISS?");
    serviceOutput.getVisualOutput().setText("The International Space Station is currently over "
        + locationName + ".\n\n Coordinates: \n "
        + astronautsResponse.getSpaceStationLocation().getLatitude() + ","
        + astronautsResponse.getSpaceStationLocation().getLongitude());
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
    if(agencyName == null){
      LaunchesResponse launchesResponse = launchLibraryClient.getUpcomingLaunchesWithCache();
      launches = launchesResponse.getLaunches();
    }else{
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
    StringBuilder outputStringBuilder = new StringBuilder("The next launch is " + launch.getName());
    if(launch.isDateLocked()){
      outputStringBuilder.append(" on ");
    }else{
      outputStringBuilder.append(", targetted for ");
    }
    outputStringBuilder.append(launchDateTime.toString(DateTimeFormat.forPattern("MMMM dd',' yyyy")));
    if(launch.isTimeLocked()){
      outputStringBuilder.append(" at" + launchDateTime.toString(DateTimeFormat.forPattern(" HH:MM")) + " Universal Time");
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
    serviceOutput.getVisualOutput().setText("Topics:\n number of people in space\n location of the ISS\n upcoming rocket launches.");
    serviceOutput.getVoiceOutput().setSsmltext("Topics are <break /> number of people in space, location of the ISS, and upcoming rocket launches");
    serviceOutput.setConversationEnded(true);
  }

  protected void doHelloRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVisualOutput().setTitle("Blast off with AstroBot!");
    serviceOutput.getVisualOutput().setText("Greetings, Earthling. What can I do for you?");
    serviceOutput.getVoiceOutput().setSsmltext("Greetings Earthling. What can I do for you?");
    serviceOutput.setConversationEnded(false);
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

  protected void doYesRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) throws DerpwizardException {
    ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(voiceInput.getMetadata().getConversationHistory(), metaSubjects);
    if(entry.getMessageSubject().equalsIgnoreCase("PEOPLE_IN_SPACE")){
      doWhoIsInSpaceRequest(voiceInput, serviceOutput);
    }else{
      serviceOutput.getVisualOutput().setTitle("Unexpected input.");
      serviceOutput.getVisualOutput().setText("I received input that seems like an affirmative answer, but I don't recall asking a question.");
      serviceOutput.getVoiceOutput().setSsmltext("I received input that seems like an affirmative answer, but I don't recall asking a question.");
      serviceOutput.setConversationEnded(true);
    }
  }

  protected void doNoRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
    doCancelRequest(serviceInput, serviceOutput);
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
}
