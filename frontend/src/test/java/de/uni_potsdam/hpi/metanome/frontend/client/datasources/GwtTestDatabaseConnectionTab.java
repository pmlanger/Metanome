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

package de.uni_potsdam.hpi.metanome.frontend.client.datasources;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;

import de.metanome.algorithm_integration.configuration.DbSystem;
import de.uni_potsdam.hpi.metanome.frontend.client.BasePage;
import de.uni_potsdam.hpi.metanome.frontend.client.TestHelper;
import de.uni_potsdam.hpi.metanome.frontend.client.helpers.InputValidationException;
import de.uni_potsdam.hpi.metanome.results_db.DatabaseConnection;
import de.uni_potsdam.hpi.metanome.results_db.EntityStorageException;

import java.util.ArrayList;


public class GwtTestDatabaseConnectionTab extends GWTTestCase {

  /**
   * Test method for {@link de.uni_potsdam.hpi.metanome.frontend.client.datasources.DatabaseConnectionTab#addDatabaseConnectionToTable(de.uni_potsdam.hpi.metanome.results_db.DatabaseConnection)}
   */
  public void testAddDatabaseConnectionToTable() {
    //Setup
    DatabaseConnection databaseConnection = new DatabaseConnection();
    databaseConnection.setUrl("url");
    databaseConnection.setPassword("password");
    databaseConnection.setUsername("user");
    databaseConnection.setSystem(DbSystem.DB2);

    DatabaseConnectionTab input = new DatabaseConnectionTab(new DataSourcePage(new BasePage()));
    int rowCount = input.connectionInputList.getRowCount();

    // Execute
    input.addDatabaseConnectionToTable(databaseConnection);

    //Check
    assertEquals(rowCount + 1, input.connectionInputList.getRowCount());
  }

  /**
   * Test method for {@link de.uni_potsdam.hpi.metanome.frontend.client.datasources.DatabaseConnectionTab#listDatabaseConnections(java.util.List)}
   */
  public void testListDatabaseConnections() {
    //Setup
    DatabaseConnection databaseConnection1 = new DatabaseConnection();
    databaseConnection1.setUrl("url");
    databaseConnection1.setPassword("password");
    databaseConnection1.setUsername("user");
    databaseConnection1.setSystem(DbSystem.DB2);

    DatabaseConnection databaseConnection2 = new DatabaseConnection();
    databaseConnection2.setUrl("url");
    databaseConnection2.setPassword("password");
    databaseConnection2.setUsername("user");
    databaseConnection2.setSystem(DbSystem.DB2);

    ArrayList<DatabaseConnection> connections = new ArrayList<DatabaseConnection>();
    connections.add(databaseConnection1);
    connections.add(databaseConnection2);

    DatabaseConnectionTab input = new DatabaseConnectionTab(new DataSourcePage(new BasePage()));

    int rowCount = input.connectionInputList.getRowCount();

    // Execute
    input.listDatabaseConnections(connections);

    //Check
    assertEquals(rowCount + 3, input.connectionInputList.getRowCount());
  }


  /**
   * Test method for {@link de.uni_potsdam.hpi.metanome.frontend.client.datasources.DatabaseConnectionTab#setEnableOfDeleteButton(de.uni_potsdam.hpi.metanome.results_db.DatabaseConnection,
   * boolean)}
   */
  public void testSetEnableDeleteButton() throws EntityStorageException, InputValidationException {
    // Setup
    TestHelper.resetDatabaseSync();

    DatabaseConnectionTab page = new DatabaseConnectionTab(new DataSourcePage(new BasePage()));

    DatabaseConnection connection = new DatabaseConnection();
    connection.setUrl("url");

    page.connectionInputList.setWidget(0, 0, new HTML("url"));
    page.connectionInputList.setText(0, 1, "user");
    page.connectionInputList.setText(0, 2, "system");
    page.connectionInputList.setWidget(0, 3, new Button("Run"));
    page.connectionInputList.setWidget(0, 4, new Button("Delete"));

    Button actualButton = (Button) page.connectionInputList.getWidget(0, 4);

    assertTrue(actualButton.isEnabled());

    // Execute
    page.setEnableOfDeleteButton(connection, false);

    // Check
    assertFalse(actualButton.isEnabled());

    // Cleanup
    TestHelper.resetDatabaseSync();
  }

  @Override
  public String getModuleName() {
    return "de.uni_potsdam.hpi.metanome.frontend.client.MetanomeTest";
  }


}