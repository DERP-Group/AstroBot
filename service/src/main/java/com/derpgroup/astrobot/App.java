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

package com.derpgroup.astrobot;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.IOException;
import java.util.logging.Logger;

import org.glassfish.jersey.filter.LoggingFilter;

import com.derpgroup.astrobot.configuration.MainConfig;
import com.derpgroup.astrobot.health.BasicHealthCheck;
import com.derpgroup.astrobot.resource.AstroBotAlexaResource;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class App extends Application<MainConfig> {

  public static void main(String[] args) throws Exception {
    new App().run(args);
  }

  @Override
  public void initialize(Bootstrap<MainConfig> bootstrap) {}

  @Override
  public void run(MainConfig config, Environment environment) throws IOException {
    ObjectMapper mapper = environment.getObjectMapper();
    if (config.isPrettyPrint()) {
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    if (config.isIgnoreUnknownJsonProperties()) {
      mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    // Health checks
    environment.healthChecks().register("basics", new BasicHealthCheck(config, environment));

    // Resources
    environment.jersey().register(new AstroBotAlexaResource(config, environment));
  }
}
