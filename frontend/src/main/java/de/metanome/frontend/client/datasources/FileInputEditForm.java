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

package de.metanome.frontend.client.datasources;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

import de.metanome.backend.input.csv.FileIterator;
import de.metanome.backend.results_db.FileInput;
import de.metanome.frontend.client.TabWrapper;
import de.metanome.frontend.client.helpers.FilePathHelper;
import de.metanome.frontend.client.helpers.InputValidationException;
import de.metanome.frontend.client.input_fields.ListBoxInput;
import de.metanome.frontend.client.services.FileInputService;
import de.metanome.frontend.client.services.FileInputServiceAsync;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Input field to configure a file input.
 */
public class FileInputEditForm extends Grid {

  /**
   * Dropdown menu for choosing a CSV file
   */
  protected ListBoxInput fileListBox;
  /**
   * Triggers whether advanced options are displayed and evaluated.
   */
  protected CheckBox advancedCheckbox;
  /**
   * Wraps all advanced options' UI elements
   */
  protected FlexTable advancedTable;
  protected TextBox separatorTextbox;
  protected TextBox quoteTextbox;
  protected TextBox escapeTextbox;
  protected IntegerBox skiplinesIntegerbox;
  protected CheckBox strictQuotesCheckbox;
  protected CheckBox ignoreLeadingWhiteSpaceCheckbox;
  protected CheckBox headerCheckbox;
  protected CheckBox skipDifferingLinesCheckbox;
  protected TextArea commentTextArea;
  private FileInputServiceAsync fileInputService;
  private FileInputTab parent;
  private String path = "";
  private TabWrapper messageReceiver;

  private List<String> filesOnStorage;
  private List<String> filesInDatabase;

  /**
   * Constructor. Set up all UI elements.
   */
  public FileInputEditForm(FileInputTab parent) {
    super(3, 2);

    this.parent = parent;
    this.fileInputService = GWT.create(FileInputService.class);

    Grid standardPanel = new Grid(3, 3);
    this.setWidget(0, 0, standardPanel);

    Button refreshButton = new Button("Refresh");
    refreshButton.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent clickEvent) {
        updateListbox();
      }
    });

    fileListBox = new ListBoxInput(false);
    updateListbox();
    standardPanel.setText(0, 0, "File Name");
    standardPanel.setWidget(0, 1, fileListBox);
    standardPanel.setWidget(0, 2, refreshButton);

    commentTextArea = new TextArea();
    commentTextArea.setVisibleLines(3);
    standardPanel.setText(1, 0, "Comment");
    standardPanel.setWidget(1, 1, commentTextArea);

    advancedCheckbox = createAdvancedCheckbox();
    standardPanel.setWidget(2, 1, advancedCheckbox);

    advancedTable = new FlexTable();
    advancedTable.setVisible(false);
    this.setWidget(1, 0, advancedTable);

    separatorTextbox = getNewOneCharTextbox();
    separatorTextbox.setName("Separator Character");
    addRow(advancedTable, separatorTextbox, "Separator Character");

    quoteTextbox = getNewOneCharTextbox();
    quoteTextbox.setName("Quote Character");
    addRow(advancedTable, quoteTextbox, "Quote Character");

    escapeTextbox = getNewOneCharTextbox();
    escapeTextbox.setName("Escape Character");
    addRow(advancedTable, escapeTextbox, "Escape Character");

    skiplinesIntegerbox = new IntegerBox();
    skiplinesIntegerbox.setWidth("5em");
    skiplinesIntegerbox.setName("Line");
    addRow(advancedTable, skiplinesIntegerbox, "Line");

    strictQuotesCheckbox = new CheckBox();
    strictQuotesCheckbox.setName("Strict Quotes");
    addRow(advancedTable, strictQuotesCheckbox, "Strict Quotes");

    ignoreLeadingWhiteSpaceCheckbox = new CheckBox();
    addRow(advancedTable, ignoreLeadingWhiteSpaceCheckbox, "Ignore Leading Whitespace");

    headerCheckbox = new CheckBox();
    addRow(advancedTable, headerCheckbox, "Has Header");

    skipDifferingLinesCheckbox = new CheckBox();
    addRow(advancedTable, skipDifferingLinesCheckbox, "Skip Lines With Differing Length");

    setDefaultAdvancedSettings();

    this.setWidget(2, 0, new Button("Save", new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        saveFileInput();
      }
    }));
  }

  /**
   * Stores the current file input in the database.
   */
  private void saveFileInput() {
    messageReceiver.clearErrors();
    try {
      this.fileInputService.storeFileInput(this.getValue(), new AsyncCallback<FileInput>() {
        @Override
        public void onFailure(Throwable throwable) {
          messageReceiver.addErrorHTML("File Input could not be stored: " + throwable.getMessage());
        }

        @Override
        public void onSuccess(FileInput input) {
          reset();
          parent.addFileInputToTable(input);
          parent.updateDataSourcesOnRunConfiguration();
          advancedTable.setVisible(false);
          advancedCheckbox.setValue(false);
          setDefaultAdvancedSettings();
          updateListbox();
        }
      });
    } catch (InputValidationException e) {
      messageReceiver.addErrorHTML("Invalid Input: " + e.getMessage());
    }
  }

  /**
   * Sets the default values of the advanced settings
   */
  private void setDefaultAdvancedSettings() {
    setSeparator(CSVParser.DEFAULT_SEPARATOR);
    setQuotechar(CSVParser.DEFAULT_QUOTE_CHARACTER);
    setEscapechar(CSVParser.DEFAULT_ESCAPE_CHARACTER);
    setSkipLines(CSVReader.DEFAULT_SKIP_LINES);
    setStrictQuotes(CSVParser.DEFAULT_STRICT_QUOTES);
    setIgnoreLeadingWhiteSpace(CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE);
    setHasHeader(FileIterator.DEFAULT_HAS_HEADER);
    setSkipDifferingLines(FileIterator.DEFAULT_SKIP_DIFFERING_LINES);
  }

  /**
   * Add another user input row to the bottom of the given table
   *
   * @param table       the UI object on which to add the row
   * @param inputWidget the widget to be used for input
   * @param name        the name of the input property
   */
  protected void addRow(FlexTable table, Widget inputWidget, String name) {
    int row = table.getRowCount();
    table.setText(row, 0, name);
    table.setWidget(row, 1, inputWidget);
  }

  /**
   * Creates a UI element for one-character user input
   *
   * @return a TextBox with width and input length limited to 2 (>1 to allow for escape characters)
   */
  private TextBox getNewOneCharTextbox() {
    TextBox textbox = new TextBox();
    textbox.setMaxLength(1);
    textbox.setWidth("2em");
    return textbox;
  }

  /**
   * Create the CheckBox that triggers the display/hiding of advanced CSV configuration parameters
   *
   * @return the CheckBox with the mentioned behavior
   */
  protected CheckBox createAdvancedCheckbox() {
    CheckBox checkbox = new CheckBox("Use Advanced Configuration");
    checkbox.setValue(false);
    checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

      @Override
      public void onValueChange(ValueChangeEvent<Boolean> event) {
        advancedTable.setVisible(advancedCheckbox.getValue());
      }
    });

    return checkbox;
  }

  /**
   * Finds all available CSV files and adds them to a drop-down menu with an empty entry ("--"),
   * which is selected by default but cannot be selected (it is disabled because it does not
   * represent a valid input file).
   *
   * @return a GWT ListBox containing all currently available CSV files
   */
  private void updateListbox() {
    this.fileListBox.clear();
    this.fileListBox.addValue("--");
    this.fileListBox.disableFirstEntry();

    this.filesOnStorage = new ArrayList<>();
    this.filesInDatabase = new ArrayList<>();

    // Add available CSV files
    AsyncCallback<String[]> storageCallback = getStorageCallback();
    AsyncCallback<List<FileInput>> databaseCallback = getDatabaseCallback();
    FileInputServiceAsync service = GWT.create(FileInputService.class);
    service.listFileInputs(databaseCallback);
    service.listCsvFiles(storageCallback);
  }

  private AsyncCallback<List<FileInput>> getDatabaseCallback() {
    return new AsyncCallback<List<FileInput>>() {
      public void onFailure(Throwable caught) {
      }

      public void onSuccess(List<FileInput> result) {
        List<String> fileNames = new ArrayList<>();

        for (FileInput input : result)
          filesInDatabase.add(input.getIdentifier());

        for (String path : filesOnStorage) {
          if (!filesInDatabase.contains(path)) {
            fileNames.add(FilePathHelper.getFileName(path));
          }
        }

        fileListBox.setValues(fileNames);
      }
    };
  }

  /**
   * Creates the callback for getting all available csv files. On success the files are added to the
   * according list box.
   *
   * @return the callback
   */
  protected AsyncCallback<String[]> getStorageCallback() {
    return new AsyncCallback<String[]>() {
      public void onFailure(Throwable caught) {
        messageReceiver.addErrorHTML("Could not find CSV files! Please add them to the input folder.");
      }

      public void onSuccess(String[] result) {
        List<String> fileNames = new ArrayList<>();

        if (result.length == 0) {
          messageReceiver
              .addError("Could not find CSV files! Please add them to the input folder.");
          return;
        }

        path = FilePathHelper.getFilePath(result[0]);

        for (String path : result) {
          if (filesInDatabase.size() > 0 && !filesInDatabase.contains(path)) {
            fileNames.add(FilePathHelper.getFileName(path));
          }

          filesOnStorage.add(path);
        }

        fileListBox.setValues(fileNames);
      }
    };
  }

  /**
   * @return a FileInput with the selected File and the advanced settings, if selected
   * @throws InputValidationException if the file name is invalid
   */
  public FileInput getValue() throws InputValidationException {
    FileInput fileInput = new FileInput();

    String fileName = this.fileListBox.getSelectedValue();
    String comment = this.commentTextArea.getValue();

    if (fileName.isEmpty() || fileName.equals("--")) {
      throw new InputValidationException("The file name is invalid.");
    }

    fileInput.setFileName(this.path + fileName);
    fileInput.setComment(comment);

    if (this.advancedCheckbox.getValue()) {
      return setAdvancedSettings(fileInput);
    }

    return fileInput;
  }

  /**
   * Setting the advanced settings at the given file input.
   *
   * @param fileInput the file input at which the advanced settings should be set
   * @return the file input with set advanced settings
   */
  private FileInput setAdvancedSettings(FileInput fileInput) throws InputValidationException {
    fileInput.setEscapechar(getChar(this.escapeTextbox));
    fileInput.setHasHeader(this.headerCheckbox.getValue());
    fileInput.setIgnoreLeadingWhiteSpace(this.ignoreLeadingWhiteSpaceCheckbox.getValue());
    fileInput.setQuotechar(getChar(this.quoteTextbox));
    fileInput.setSeparator(getChar(this.separatorTextbox));
    fileInput.setSkipDifferingLines(this.skipDifferingLinesCheckbox.getValue());
    fileInput.setSkipLines(getInteger(this.skiplinesIntegerbox));
    fileInput.setStrictQuotes(this.strictQuotesCheckbox.getValue());

    return fileInput;
  }

  /**
   * Checks, if the given text box contains only a character. If yes, the character is returned.
   * Otherwise an exception is thrown.
   *
   * @return the character of the text box
   */
  private char getChar(TextBox textBox) throws InputValidationException {
    String value = textBox.getValue();

    if (value.length() != 1) {
      throw new InputValidationException(textBox.getName() + " should only contain one character!");
    }

    return value.charAt(0);
  }

  /**
   * Checks, if the value of the integer box is an integer. If yes, the integer is returned.
   * Otherwise an exception is thrown.
   *
   * @return the integer of the integer box
   */
  private int getInteger(IntegerBox integerBox) throws InputValidationException {
    try {
      return integerBox.getValueOrThrow();
    } catch (ParseException e) {
      throw new InputValidationException(integerBox.getName() + " should only contain numbers!");
    }
  }

  protected void setFileName(String fileName) {
    this.fileListBox.addValue(fileName);
    this.fileListBox.setSelectedValue(fileName);
  }

  protected void setSeparator(char separator) {
    this.separatorTextbox.setValue(String.valueOf(separator));
  }

  protected void setEscapechar(char escapechar) {
    this.escapeTextbox.setValue(String.valueOf(escapechar));
  }

  protected void setQuotechar(char quotechar) {
    this.quoteTextbox.setValue(String.valueOf(quotechar));
  }

  protected void setSkipLines(int skipLines) {
    this.skiplinesIntegerbox.setValue(skipLines);
  }

  protected void setStrictQuotes(boolean strictQuotes) {
    this.strictQuotesCheckbox.setValue(strictQuotes);
  }

  protected void setIgnoreLeadingWhiteSpace(boolean ignoreLeadingWhiteSpace) {
    this.ignoreLeadingWhiteSpaceCheckbox.setValue(ignoreLeadingWhiteSpace);
  }

  protected void setHasHeader(boolean hasHeader) {
    this.headerCheckbox.setValue(hasHeader);
  }

  protected void setSkipDifferingLines(boolean skipDifferingLines) {
    this.skipDifferingLinesCheckbox.setValue(skipDifferingLines);
  }

  /**
   * Reset the file name to the default entry "--" in the list box.
   */
  public void reset() {
    this.commentTextArea.setText("");
    this.fileListBox.reset();
    this.setDefaultAdvancedSettings();
  }

  /**
   * Set the message receiver.
   *
   * @param tab the message receiver tab wrapper
   */
  public void setMessageReceiver(TabWrapper tab) {
    this.messageReceiver = tab;
  }
}
