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

package de.metanome.backend.results_db;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.gwt.user.client.rpc.IsSerializable;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

import de.metanome.backend.input.csv.FileIterator;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Represents file inputs in the database.
 *
 * @author Jakob Zwiener
 */
@Entity
@GwtCompatible
public class FileInput extends Input implements IsSerializable {

  private static final long serialVersionUID = 2320081610461965426L;

  protected String fileName;
  protected char separator;
  protected char quotechar;
  protected char escapechar;
  protected int skipLines;
  protected boolean strictQuotes;
  protected boolean ignoreLeadingWhiteSpace;
  protected boolean hasHeader;
  protected boolean skipDifferingLines;
  protected String comment;

  /**
   * Constructs a FileInput with default parser settings.
   */
  public FileInput() {
    this.separator = CSVParser.DEFAULT_SEPARATOR;
    this.quotechar = CSVParser.DEFAULT_QUOTE_CHARACTER;
    this.escapechar = CSVParser.DEFAULT_ESCAPE_CHARACTER;
    this.skipLines = CSVReader.DEFAULT_SKIP_LINES;
    this.strictQuotes = CSVParser.DEFAULT_STRICT_QUOTES;
    this.ignoreLeadingWhiteSpace = CSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE;
    this.hasHeader = FileIterator.DEFAULT_HAS_HEADER;
    this.skipDifferingLines = FileIterator.DEFAULT_SKIP_DIFFERING_LINES;
  }

  /**
   * Constructs a FileInput with a given file name. Default parser settings are set.
   *
   * @param fileName input's file name
   */
  public FileInput(String fileName) {
    this();
    this.fileName = fileName;

  }

  /**
   * Retrieves a FileInput from the database.
   *
   * @param id the FileInput's id
   * @return the fileInput
   */
  @GwtIncompatible("HibernateUtil is not gwt compatible.")
  public static FileInput retrieve(long id) throws EntityStorageException {
    return (FileInput) HibernateUtil.retrieve(FileInput.class, id);
  }

  /**
   * Retrieves all file inputs stored in the database.
   *
   * @return a list of all file inputs
   */
  @GwtIncompatible("HibernateUtil is not gwt compatible.")
  public static List<Input> retrieveAll() throws EntityStorageException {
    return HibernateUtil.queryCriteria(FileInput.class);
  }

  /**
   * Stores the FileInput in the database.
   *
   * @return the FileInput
   */
  @Override
  @GwtIncompatible("HibernateUtil is not gwt compatible.")
  public FileInput store() throws EntityStorageException {
    HibernateUtil.store(this);

    return this;
  }

  public String getFileName() {
    return fileName;
  }

  public FileInput setFileName(String fileName) {
    this.fileName = fileName;

    return this;
  }

  public char getSeparator() {
    return separator;
  }

  public FileInput setSeparator(char separator) {
    this.separator = separator;

    return this;
  }

  public char getQuotechar() {
    return quotechar;
  }

  public FileInput setQuotechar(char quotechar) {
    this.quotechar = quotechar;

    return this;
  }

  public char getEscapechar() {
    return escapechar;
  }

  public FileInput setEscapechar(char escapechar) {
    this.escapechar = escapechar;

    return this;
  }

  public int getSkipLines() {
    return skipLines;
  }

  public FileInput setSkipLines(int skipLines) {
    this.skipLines = skipLines;

    return this;
  }

  public boolean isStrictQuotes() {
    return strictQuotes;
  }

  public FileInput setStrictQuotes(boolean strictQuotes) {
    this.strictQuotes = strictQuotes;

    return this;
  }

  public boolean isIgnoreLeadingWhiteSpace() {
    return ignoreLeadingWhiteSpace;
  }

  public FileInput setIgnoreLeadingWhiteSpace(boolean ignoreLeadingWhiteSpace) {
    this.ignoreLeadingWhiteSpace = ignoreLeadingWhiteSpace;

    return this;
  }

  public boolean isHasHeader() {
    return hasHeader;
  }

  public FileInput setHasHeader(boolean hasHeader) {
    this.hasHeader = hasHeader;

    return this;
  }

  public boolean isSkipDifferingLines() {
    return skipDifferingLines;
  }

  public FileInput setSkipDifferingLines(boolean skipDifferingLines) {
    this.skipDifferingLines = skipDifferingLines;

    return this;
  }

  public String getComment() {
    return comment;
  }

  public FileInput setComment(String comment) {
    this.comment = comment;

    return this;
  }

  @Override
  public FileInput setId(long id) {
    super.setId(id);

    return this;
  }

  @Override
  @Transient
  public String getIdentifier() {
    return fileName;
  }

}
