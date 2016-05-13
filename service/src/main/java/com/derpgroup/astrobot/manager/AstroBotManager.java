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

import com.derpgroup.astrobot.configuration.MainConfig;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;

/**
 * Manager class for dispatching input messages.
 *
 * @author David
 * @author Eric
 * @since 0.0.1
 */
public class AstroBotManager {

  public AstroBotManager(MainConfig config) {
    // TODO Auto-generated constructor stub
  }

  /**
   * An example primary entry point into the service.
   * At this point the Resource classes should have mapped any device-specific requests
   * into standard ServiceInput/ServiceOutput POJOs. As well as mapped any device-specific
   * requests into service understandable subjects.
   * @param serviceInput
   * @param serviceOutput
   */
  public void handleRequest(ServiceInput serviceInput, ServiceOutput serviceOutput){
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

  private void doPeopleInSpaceRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVisualOutput().setTitle("People in Space");
    serviceOutput.getVisualOutput().setText("There are currently 6 people in space.");
    serviceOutput.getVoiceOutput().setSsmltext("There are currently six people in space.");
  }

  private void doInternationalSpaceStationRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVisualOutput().setTitle("Where is the ISS?");
    serviceOutput.getVisualOutput().setText("The International Space Station is currently over Seattle.");
    serviceOutput.getVoiceOutput().setSsmltext("The ISS is currently over Seattle.");
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
