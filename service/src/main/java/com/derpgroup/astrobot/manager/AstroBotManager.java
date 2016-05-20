/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
 * Copyright (C) 2015 Rusty Gerard
 * Copyright (C) 2015 Paul Winters
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.derpgroup.astrobot.MixInModule;
import com.derpgroup.astrobot.configuration.MainConfig;
import com.derpgroup.astrobot.util.opennotify.AstronautsResponse;
import com.derpgroup.astrobot.util.opennotify.OpenNotifyClient;
import com.derpgroup.astrobot.util.opennotify.SpaceStationLocationResponse;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
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

  static {
    ConversationHistoryUtils.getMapper().registerModule(new MixInModule());
  }
  private OpenNotifyClient openNotifyClient;
  private GeoApiContext googleMapsGeoApiContext;
  
  public AstroBotManager(MainConfig config) {
    String openNotifyApiRootUrl = config.getAstroBotConfig().getOpenNotifyConfig().getOpenNotifyApiRootUrl();
    openNotifyClient = new OpenNotifyClient(openNotifyApiRootUrl);
    String googleMapsApiKey = config.getAstroBotConfig().getGoogleMapsConfig().getApiKey();
    googleMapsGeoApiContext = new GeoApiContext().setApiKey(googleMapsApiKey);
  }

  /**
   * An example primary entry point into the service.
   * At this point the Resource classes should have mapped any device-specific requests
   * into standard ServiceInput/ServiceOutput POJOs. As well as mapped any device-specific
   * requests into service understandable subjects.
   * @param serviceInput
   * @param serviceOutput
   */
  public void handleRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException{
    switch(serviceInput.getSubject()){
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
    case "PEOPLE_IN_SPACE":
      doPeopleInSpaceRequest(serviceInput, serviceOutput);
      break;
    case "INTERNATIONAL_SPACE_STATION":
      doInternationalSpaceStationRequest(serviceInput, serviceOutput);
      break;
    case "NEXT_LAUNCH":
      doNextLaunchRequest(serviceInput, serviceOutput);
      break;
    default:
      break;
    }
  }

  private void doPeopleInSpaceRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException{
    AstronautsResponse astronautsResponse = openNotifyClient.getAstronauts();
    serviceOutput.getVisualOutput().setTitle("People in Space");
    serviceOutput.getVisualOutput().setText("There are currently " + astronautsResponse.getNumber() + " people in space.");
    serviceOutput.getVoiceOutput().setSsmltext("There are currently " + astronautsResponse.getNumber() + " people in space.");
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

  private void doNextLaunchRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVisualOutput().setTitle("Next Launch:");
    serviceOutput.getVisualOutput().setText("The next launch is May 26");
    serviceOutput.getVoiceOutput().setSsmltext("The next launch is May 26");
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
    serviceOutput.getVoiceOutput().setSsmltext("Greetings, Earthling. What can I do for you?");
    serviceOutput.setConversationEnded(false);
  }

  protected void doGoodbyeRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("AstroBot over and out.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doCancelRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Copy that, Houston.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doStopRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Roger that.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doRepeatRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.setConversationEnded(true);
  }

  protected void doYesRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.setConversationEnded(true);
  }

  protected void doNoRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.setConversationEnded(true);
  }
}
