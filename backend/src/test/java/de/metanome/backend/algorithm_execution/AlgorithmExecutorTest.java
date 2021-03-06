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

package de.metanome.backend.algorithm_execution;

import de.metanome.algorithm_integration.AlgorithmExecutionException;
import de.metanome.algorithm_integration.algorithm_execution.FileGenerator;
import de.metanome.algorithm_integration.configuration.ConfigurationRequirement;
import de.metanome.algorithm_integration.configuration.ConfigurationRequirementFileInput;
import de.metanome.algorithm_integration.configuration.ConfigurationRequirementRelationalInput;
import de.metanome.algorithm_integration.configuration.ConfigurationSettingFileInput;
import de.metanome.algorithm_integration.configuration.ConfigurationValue;
import de.metanome.algorithm_integration.input.FileInputGenerator;
import de.metanome.algorithm_integration.input.RelationalInputGenerator;
import de.metanome.algorithm_integration.results.BasicStatistic;
import de.metanome.algorithm_integration.results.FunctionalDependency;
import de.metanome.algorithm_integration.results.InclusionDependency;
import de.metanome.algorithm_integration.results.OrderDependency;
import de.metanome.algorithm_integration.results.UniqueColumnCombination;
import de.metanome.algorithms.testing.example_basic_stat_algorithm.BasicStatAlgorithm;
import de.metanome.algorithms.testing.example_ind_algorithm.ExampleAlgorithm;
import de.metanome.algorithms.testing.example_relational_input_algorithm.RelationalInputAlgorithm;
import de.metanome.backend.algorithm_loading.AlgorithmLoadingException;
import de.metanome.backend.configuration.ConfigurationValueFileInputGenerator;
import de.metanome.backend.configuration.ConfigurationValueInteger;
import de.metanome.backend.configuration.ConfigurationValueListBox;
import de.metanome.backend.configuration.ConfigurationValueRelationalInputGenerator;
import de.metanome.backend.configuration.ConfigurationValueString;
import de.metanome.backend.input.csv.FileFixture;
import de.metanome.backend.result_receiver.CloseableOmniscientResultReceiver;
import de.metanome.backend.results_db.Algorithm;
import de.metanome.backend.results_db.EntityStorageException;
import de.metanome.backend.results_db.Execution;
import de.metanome.backend.results_db.HibernateUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor}
 */
public class AlgorithmExecutorTest {

  protected CloseableOmniscientResultReceiver resultReceiver;
  protected ProgressCache progressCache;
  protected FileGenerator fileGenerator;

  protected AlgorithmExecutor executor;

  @Before
  public void setUp() throws UnsupportedEncodingException {
    resultReceiver = mock(CloseableOmniscientResultReceiver.class);
    progressCache = mock(ProgressCache.class);
    fileGenerator = new TempFileGenerator();

    executor = new AlgorithmExecutor(resultReceiver, progressCache, fileGenerator);
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithm(String,
   * List)} <p/> Tests the execution of an fd algorithm. The elapsed time should be greater than
   * 0ns.
   */
  @Test
  public void testExecuteFunctionalDependencyAlgorithm()
      throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException,
             SecurityException, IOException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException,
             EntityStorageException {
    // Setup
    List<ConfigurationValue> configs = new ArrayList<>();
    configs.add(new ConfigurationValueString("pathToOutputFile", "path/to/file"));
    String[] selectedValues = {"second"};
    configs.add(new ConfigurationValueListBox("column names", selectedValues));
    String algorithmFileName = "example_fd_algorithm.jar";
    new Algorithm(algorithmFileName).store();

    // Execute functionality
    long elapsedTime = executor.executeAlgorithmWithValues(algorithmFileName, configs);

    // Check result
    verify(resultReceiver).receiveResult(isA(FunctionalDependency.class));
    assertTrue(0 <= elapsedTime);
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithm(String,
   * List)} <p/> Tests the execution of an od algorithm. The elapsed time should be greater than
   * 0ns.
   */
  @Test
  public void testExecuteOrderDependencyAlgorithm()
      throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException,
             SecurityException, IOException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException,
             EntityStorageException {
    // Setup
    List<ConfigurationValue> configs = new ArrayList<>();
    configs.add(new ConfigurationValueString(de.metanome.algorithms.testing.example_od_algorithm.ExampleAlgorithm.FILE_NAME, "path/to/file"));
    String algorithmFileName = "example_od_algorithm.jar";
    new Algorithm(algorithmFileName).store();

    // Execute functionality
    long elapsedTime = executor.executeAlgorithmWithValues(algorithmFileName, configs);

    // Check result
    verify(resultReceiver).receiveResult(isA(OrderDependency.class));
    assertTrue(0 <= elapsedTime);
  }
  
  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithmWithValues(String,
   * java.util.List)} <p/> Tests the execution of an ind algorithm.
   */
  @Test
  public void testExecuteInclusionDependency()
      throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException,
             SecurityException, IOException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException,
             EntityStorageException {
    // Setup
    List<ConfigurationValue> configs = new ArrayList<>();
    configs.add(new ConfigurationValueString(ExampleAlgorithm.STRING_IDENTIFIER, "table1"));
    configs.add(new ConfigurationValueInteger(ExampleAlgorithm.INTEGER_IDENTIFIER, 7));
    configs.add(new ConfigurationValueFileInputGenerator(
        ExampleAlgorithm.CSV_FILE_IDENTIFIER,
        mock(FileInputGenerator.class)));
    String algorithmFileName = "example_ind_algorithm.jar";
    new Algorithm(algorithmFileName)
        .store();

    // Execute functionality
    executor.executeAlgorithmWithValues(algorithmFileName, configs);

    // Check result
    verify(resultReceiver).receiveResult(isA(InclusionDependency.class));
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithm(String,
   * java.util.List)}
   *
   * The {@link de.metanome.algorithms.testing.example_relational_input_algorithm.RelationalInputAlgorithm}
   * should be executable by generating a {@link de.metanome.algorithm_integration.input.RelationalInputGenerator}
   * from a file.
   */
  @Test
  public void testRelationalInputAlgorithm()
      throws AlgorithmExecutionException, AlgorithmLoadingException, EntityStorageException,
             FileNotFoundException, UnsupportedEncodingException {
    // Setup
    String algorithmFileName = "example_relational_input_algorithm.jar";
    String path = new FileFixture("some file content").getTestData("some file name").getPath();
    List<ConfigurationRequirement> requirements = new ArrayList<>();
    ConfigurationRequirementRelationalInput
        requirementRelationalInput =
        new ConfigurationRequirementRelationalInput(
            RelationalInputAlgorithm.RELATIONAL_INPUT_IDENTIFIER);
    requirementRelationalInput.setSettings(new ConfigurationSettingFileInput(path));
    requirements.add(requirementRelationalInput);
    new Algorithm(algorithmFileName)
        .store();

    // Execute functionality
    // Check result
    executor.executeAlgorithm(algorithmFileName, requirements);
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithmWithValues(String,
   * java.util.List)} <p/> Tests the execution of an ucc algorithm.
   */
  @Test
  public void testExecuteUniqueColumnCombinationsAlgorithm()
      throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException,
             SecurityException, IOException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException,
             EntityStorageException {
    // Setup
    List<ConfigurationValue> configs = new ArrayList<>();
    configs.add(new ConfigurationValueString("pathToInputFile", "path/to/file1", "path/to/file2"));
    String algorithmFileName = "example_ucc_algorithm.jar";
    new Algorithm(algorithmFileName).store();

    // Execute functionality
    executor.executeAlgorithmWithValues(algorithmFileName, configs);

    // Check result
    verify(resultReceiver).receiveResult(isA(UniqueColumnCombination.class));
    // After finishing the progress should be 1;
    verify(progressCache).updateProgress(1);
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithmWithValues(String,
   * java.util.List)} <p/> Tests the execution of an holistic algorithm.
   */
  @Test
  public void testExecuteHolisticAlgorithm()
      throws AlgorithmLoadingException, AlgorithmExecutionException, IllegalArgumentException,
             SecurityException, IOException, ClassNotFoundException, InstantiationException,
             IllegalAccessException, InvocationTargetException, NoSuchMethodException,
             EntityStorageException {
    // Setup
    List<ConfigurationValue> configs = new ArrayList<>();
    configs.add(new ConfigurationValueString("pathToOutputFile", "path/to/file1"));
    String algorithmFileName = "example_holistic_algorithm.jar";
    new Algorithm(algorithmFileName).store();

    // Execute functionality
    executor.executeAlgorithmWithValues(algorithmFileName, configs);

    // Check result
    verify(resultReceiver).receiveResult(isA(FunctionalDependency.class));
    verify(resultReceiver).receiveResult(isA(UniqueColumnCombination.class));
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithmWithValues(String,
   * java.util.List)} <p/> Algorithms that do not implement the metanome interfaces directly should
   * still be executable.
   */
  @Test
  public void testExecuteIndirectInterfaceAlgorithm()
      throws IllegalAccessException, IOException, InstantiationException,
             AlgorithmExecutionException, NoSuchMethodException, InvocationTargetException,
             ClassNotFoundException, EntityStorageException {
    // Setup
    List<ConfigurationValue> configurationValues = new LinkedList<>();
    configurationValues.add(new ConfigurationValueRelationalInputGenerator("identifier", mock(
        RelationalInputGenerator.class)));
    String algorithmFileName = "example_indirect_interfaces_algorithm.jar";
    new Algorithm(algorithmFileName)
        .store();

    // Execute functionality
    executor.executeAlgorithmWithValues(algorithmFileName, configurationValues);

    // Check result
    verify(resultReceiver).receiveResult(isA(UniqueColumnCombination.class));
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithmWithValues(String,
   * java.util.List)} <p/> When executing an {@link de.metanome.algorithm_integration.Algorithm} an
   * {@link de.metanome.backend.results_db.Execution} should be saved in the results database.
   */
  @Test
  public void testExecutionStoredInDatabase()
      throws IllegalAccessException, IOException, InstantiationException,
             AlgorithmExecutionException, NoSuchMethodException, InvocationTargetException,
             ClassNotFoundException, EntityStorageException {
    // Setup
    HibernateUtil.clear();

    List<ConfigurationValue> configurationValues = new LinkedList<>();
    configurationValues.add(new ConfigurationValueRelationalInputGenerator("identifier", mock(
        RelationalInputGenerator.class)));
    String algorithmFileName = "example_indirect_interfaces_algorithm.jar";
    Algorithm expectedAlgorithm = new Algorithm(algorithmFileName).store();

    // Execute functionality
    executor.executeAlgorithmWithValues(algorithmFileName, configurationValues);
    List<Execution> actualExecutions = Execution.retrieveAll();

    // Check result
    assertFalse(actualExecutions.isEmpty());
    Execution actualExecution = actualExecutions.get(0);
    assertEquals(expectedAlgorithm, actualExecution.getAlgorithm());
    // The execution should not be older than 5 seconds.
    assertTrue(new Date().getTime() - actualExecution.getBegin().getTime() < 5000);
    assertTrue(new Date().getTime() - actualExecution.getEnd().getTime() < 5000);
    // The execution should have taken between 0 and 3 seconds.
    assertTrue(actualExecution.getEnd().getTime() - actualExecution.getBegin().getTime() < 3000);
    assertTrue(actualExecution.getEnd().getTime() - actualExecution.getBegin().getTime() > 0);
    // TODO assert other execution fields

    // Cleanup
    HibernateUtil.clear();
  }

  //FIXME add test for incorrect file name

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#executeAlgorithm(String,
   * java.util.List)} <p/> Tests the execution of a basic statistics algorithm that requires several
   * {@link de.metanome.algorithm_integration.input.FileInputGenerator}s to run.
   */
  @Test
  public void testExecuteBasicStatisticsAlgorithmWithFileInputGenerator()
      throws AlgorithmExecutionException, AlgorithmLoadingException, FileNotFoundException,
             UnsupportedEncodingException, EntityStorageException {
    // Setup
    // Build file input specification
    int numberOfInputs = 5;
    List<ConfigurationRequirement> configurationRequirements = new LinkedList<>();
    ConfigurationRequirementFileInput
        specification =
        new ConfigurationRequirementFileInput(BasicStatAlgorithm.INPUT_FILE_IDENTIFIER,
                                              numberOfInputs);

    // Build input files
    String expectedStatisticValue = "some value";
    String expectedOtherFileName = "some other file name";
    FileFixture fileFixture = new FileFixture("some file content");
    expectedOtherFileName = fileFixture.getTestData(expectedOtherFileName).getAbsolutePath();
    expectedStatisticValue = fileFixture.getTestData(expectedStatisticValue).getAbsolutePath();

    // Build mock configuration settings
    ConfigurationSettingFileInput[] settings = new ConfigurationSettingFileInput[numberOfInputs];
    for (int i = 0; i < numberOfInputs - 1; i++) {
      ConfigurationSettingFileInput
          configurationSetting =
          mock(ConfigurationSettingFileInput.class);
      when(configurationSetting.isAdvanced()).thenReturn(false);
      when(configurationSetting.getFileName()).thenReturn(expectedOtherFileName);
      settings[i] = configurationSetting;
    }
    // Last setting determines algorithm's result
    ConfigurationSettingFileInput lastSetting = mock(ConfigurationSettingFileInput.class);
    when(lastSetting.isAdvanced()).thenReturn(false);
    when(lastSetting.getFileName()).thenReturn(expectedStatisticValue);
    settings[4] = lastSetting;
    specification.setSettings(settings);

    configurationRequirements.add(specification);

    String algorithmFileName = "example_basic_stat_algorithm.jar";
    new Algorithm(algorithmFileName).store();

    // Execute functionality
    executor.executeAlgorithm(algorithmFileName, configurationRequirements);

    // Check result
    ArgumentCaptor<BasicStatistic> captor = ArgumentCaptor.forClass(BasicStatistic.class);
    verify(resultReceiver).receiveResult(captor.capture());
    assertEquals(expectedStatisticValue, captor.getValue().getStatisticValue());
  }

  /**
   * Test method for {@link de.metanome.backend.algorithm_execution.AlgorithmExecutor#close()} <p/>
   * When closing the executor all attached result receiver should be closed.
   */
  @Test
  public void testClose() throws IOException {
    // Execute functionality
    executor.close();

    // Check result
    verify(resultReceiver).close();
  }

}
