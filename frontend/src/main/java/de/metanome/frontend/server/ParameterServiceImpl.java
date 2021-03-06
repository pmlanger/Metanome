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

package de.metanome.frontend.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.metanome.algorithm_integration.Algorithm;
import de.metanome.algorithm_integration.AlgorithmExecutionException;
import de.metanome.algorithm_integration.configuration.ConfigurationRequirement;
import de.metanome.backend.algorithm_loading.AlgorithmJarLoader;
import de.metanome.backend.helper.ExceptionParser;
import de.metanome.frontend.client.services.ParameterService;

import java.util.List;

/**
 * Service Implementation that provides functionality for retrieving and setting parameters on
 * algorithms.
 */
public class ParameterServiceImpl extends RemoteServiceServlet implements ParameterService {

  private static final long serialVersionUID = 7343803695093136183L;

  /**
   * Loads an algorithm and its configuration requirements
   *
   * @param algorithmFileName name of the algorithm for which the configuration parameters shall be
   *                          retrieved
   * @return a list of {@link de.metanome.algorithm_integration.configuration.ConfigurationRequirement}s
   * necessary for calling the given algorithm
   */
  @Override
  public List<ConfigurationRequirement> retrieveParameters(String algorithmFileName)
      throws AlgorithmExecutionException {
    Algorithm algorithm = null;
    AlgorithmJarLoader jarLoader = new AlgorithmJarLoader();
    try {
      algorithm = jarLoader.loadAlgorithm(algorithmFileName);
    } catch (Exception e) {
      throw new AlgorithmExecutionException(ExceptionParser.parse(e), e);
    }

    List<ConfigurationRequirement> configList = algorithm.getConfigurationRequirements();

    return configList;
  }
}
