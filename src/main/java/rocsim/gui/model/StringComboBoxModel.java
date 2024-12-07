/*
 * Copyright © 2024 Oliver Schünemann (oschuen@users.noreply.github.com)
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
package rocsim.gui.model;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

public class StringComboBoxModel implements ComboBoxModel<String> {

  private StringListDataModel selectionModel;
  private String selectedLocoId = "";

  public StringComboBoxModel(StringListDataModel selectionModel) {
    this.selectionModel = selectionModel;
  }

  @Override
  public void addListDataListener(ListDataListener arg0) {
    this.selectionModel.addListDataListener(arg0);

  }

  @Override
  public String getElementAt(int arg0) {
    return this.selectionModel.getElementAt(arg0);
  }

  @Override
  public int getSize() {
    return this.selectionModel.getSize();
  }

  @Override
  public void removeListDataListener(ListDataListener arg0) {
    this.selectionModel.removeListDataListener(arg0);

  }

  @Override
  public String getSelectedItem() {
    return this.selectedLocoId;
  }

  @Override
  public void setSelectedItem(Object arg0) {
    this.selectedLocoId = (String) arg0;

  }

}