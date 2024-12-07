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
package rocsim.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.LocoModel;

public class LocoPanel extends DataPanel {

  private static final long serialVersionUID = 1L;
  private JTextField idTextField;
  private JFormattedTextField vMaxTextField;
  private JTextField commentTextField;

  public static class LocoPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<LocoPanel> {

    @Override
    public LocoPanel createNewItem() {
      return new LocoPanel();
    }
  };

  public LocoPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    JLabel lblNewLabel = new JLabel("Loco Id");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 5, 0, 10);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    this.idTextField = new JTextField();
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.anchor = GridBagConstraints.WEST;
    gbc_textField.insets = new Insets(0, 0, 0, 0);
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 0;
    add(this.idTextField, gbc_textField);
    this.idTextField.setColumns(20);
    this.idTextField.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(FocusEvent arg0) {
        fireChanged();
      }

      @Override
      public void focusGained(FocusEvent arg0) {
        // TODO Auto-generated method stub

      }
    });
    JLabel vMaxPanel = new JLabel("V Max");
    GridBagConstraints gbc_Comment = new GridBagConstraints();
    gbc_Comment.anchor = GridBagConstraints.WEST;
    gbc_Comment.insets = new Insets(0, 5, 0, 10);
    gbc_Comment.gridx = 2;
    gbc_Comment.gridy = 0;

    add(vMaxPanel, gbc_Comment);

    this.vMaxTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.insets = new Insets(0, 0, 0, 0);
    gbc_textField_1.anchor = GridBagConstraints.WEST;
    gbc_textField_1.gridwidth = 3;
    gbc_textField_1.gridx = 3;
    gbc_textField_1.gridy = 0;
    add(this.vMaxTextField, gbc_textField_1);
    this.vMaxTextField.setColumns(10);

    JLabel lblNewLabel_1 = new JLabel("Comment");
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.insets = new Insets(0, 5, 0, 10);
    gbc_lblNewLabel_1.gridx = 6;
    gbc_lblNewLabel_1.gridy = 0;
    add(lblNewLabel_1, gbc_lblNewLabel_1);

    this.commentTextField = new JTextField();
    GridBagConstraints gbc_textField_2 = new GridBagConstraints();
    gbc_textField_2.weightx = 10.0;
    gbc_textField_2.insets = new Insets(0, 0, 0, 0);
    gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_2.gridx = 7;
    gbc_textField_2.gridy = 0;
    add(this.commentTextField, gbc_textField_2);
    this.commentTextField.setColumns(40);
  }

  public void setModel(LocoModel model) {
    this.idTextField.setText(model.getId());
    this.commentTextField.setText(model.getComment());
    this.vMaxTextField.setValue(model.getvMax());
  }

  public LocoModel getModel() {
    LocoModel model = new LocoModel();
    model.setId(this.idTextField.getText());
    try {
      model.setvMax(Integer.valueOf(this.vMaxTextField.getText()));
    } catch (Exception e) {
      model.setvMax(80);
    }
    model.setComment(this.commentTextField.getText());
    return model;
  }

  private void fireChanged() {
    fireDataChanged();
  }
}
