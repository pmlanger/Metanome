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

package de.metanome.frontend.client.input_fields;

import com.google.gwt.user.client.ui.IntegerBox;

import de.metanome.frontend.client.helpers.InputValidationException;

import java.text.ParseException;


/**
 * A wrapper for a integer input field.
 */
public class IntegerInput extends InputField {

  public IntegerBox textbox;

  /**
   * @param optional If true, a remove button will be rendered, to remove this widget from its
   *                 parent.
   */
  public IntegerInput(boolean optional) {
    super(optional);

    this.textbox = new IntegerBox();
    this.add(this.textbox);
  }

  /**
   * Checks if the textbox contains only numbers and returns the number or an exception if it does
   * not contain only numbers or -1 if the textbox is emtpy.
   *
   * @return the value of its text box
   * @throws de.metanome.frontend.client.helpers.InputValidationException if the value cannot be
   *                                                                      parsed as an int
   */
  public int getValue() throws InputValidationException {
    Integer val;
    try {
      val = this.textbox.getValueOrThrow();
    } catch (ParseException e) {
      throw new InputValidationException("Only numbers are allowed!", e);
    }
    if (val == null) {
      throw new InputValidationException("You have to enter a number!");
    }
    return val;
  }

  /**
   * Sets the value of the integer box
   *
   * @param value the value which should be set
   */
  public void setValue(Integer value) {
    this.textbox.setValue(value);
  }
}
