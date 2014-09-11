/*
 * Copyright 2014 by the Metanome project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.metanome.algorithm_integration.configuration;

/**
 * Concrete {@link ConfigurationRequirement} for file inputs.
 *
 * @author Jakob Zwiener
 * @see ConfigurationRequirement
 */
public class ConfigurationRequirementFileInput extends ConfigurationRequirement {

  private static final long serialVersionUID = 8842139128248338302L;

  private ConfigurationSettingFileInput[] settings;

  /**
   * Exists for GWT serialization.
   */
  public ConfigurationRequirementFileInput() {
  }

  /**
   * Constructs a {@link ConfigurationRequirementFileInput}, requesting 1 value.
   *
   * @param identifier the specification's identifier
   */
  public ConfigurationRequirementFileInput(String identifier) {
    super(identifier);
  }

  /**
   * Constructs a {@link ConfigurationRequirementFileInput}, potentially requesting several values.
   *
   * @param identifier     the specification's identifier
   * @param numberOfValues the number of values expected
   */
  public ConfigurationRequirementFileInput(String identifier,
                                           int numberOfValues) {

    super(identifier, numberOfValues);
  }

  @Override
  public ConfigurationSettingFileInput[] getSettings() {
    return this.settings;
  }

  public void setSettings(ConfigurationSettingFileInput... configurationSettings) {
    this.settings = configurationSettings;
  }
}