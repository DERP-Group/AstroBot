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

package com.derpgroup.astrobot.configuration;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.derpgroup.derpwizard.configuration.DAOConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Top-level configuration class.
 *
 * @author Eric Olson
 * @since 0.0.1
 */
public class MainConfig extends Configuration {
  private boolean prettyPrint = true;
  private DAOConfig daoConfig;
  @Valid
  @NotNull
  private AstroBotConfig astroBotConfig;

  private boolean ignoreUnknownJsonProperties;

  @JsonProperty
  public boolean isPrettyPrint() {
    return prettyPrint;
  }

  @JsonProperty
  public void setPrettyPrint(boolean prettyPrint) {
    this.prettyPrint = prettyPrint;
  }

  @JsonProperty
  public DAOConfig getDaoConfig() {
    return daoConfig;
  }

  @JsonProperty
  public void setDaoConfig(DAOConfig daoConfig) {
    this.daoConfig = daoConfig;
  }

  public AstroBotConfig getAstroBotConfig() {
    return astroBotConfig;
  }

  public void setAstroBotConfig(AstroBotConfig astroBotConfig) {
    this.astroBotConfig = astroBotConfig;
  }

  public boolean isIgnoreUnknownJsonProperties() {
    return ignoreUnknownJsonProperties;
  }

  public void setIgnoreUnknownJsonProperties(
      boolean ignoreUnknownJsonProperties) {
    this.ignoreUnknownJsonProperties = ignoreUnknownJsonProperties;
  }
}
