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
package rocsim.gui.animation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.MaskFormatter;

import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;

public class TimePanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private JFormattedTextField startTimeTextField;
  private JRadioButton rdbtnModelTime;
  private JRadioButton rdbtnRealTime;
  private JLabel lblEndTime;
  private JFormattedTextField endTimeTextField;
  private TimeModel timeModel;

  private void updateTimeFields() {
    TimePanel.this.startTimeTextField.setValue(this.timeModel.getTimeSecString(this.timeModel.getMinTime()));
    if (this.timeModel.isDisplayRealTime()) {
      TimePanel.this.endTimeTextField.setValue(this.timeModel.getTimeSecString(this.timeModel.getMaxTime()));
    } else {
      TimePanel.this.endTimeTextField
          .setValue(this.timeModel.getTimeSecString(this.timeModel.toFremoTime(this.timeModel.getMaxTime())));
    }
  }

  public TimePanel(TimeModel timeModel) {
    this.timeModel = timeModel;
    timeModel.addListener(new TimeModelChangeListener() {

      @Override
      public void timeModelChanged() {
        updateTimeFields();
      }
    });
    MaskFormatter mask = null;
    try {
      mask = new MaskFormatter("##:##:##");
      mask.setPlaceholderCharacter('#');
    } catch (ParseException e) {
    }

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    this.rdbtnRealTime = new JRadioButton("Real Time");
    GridBagConstraints gbc_rdbtnRealTime = new GridBagConstraints();
    gbc_rdbtnRealTime.anchor = GridBagConstraints.WEST;
    gbc_rdbtnRealTime.insets = new Insets(0, 0, 5, 5);
    gbc_rdbtnRealTime.gridx = 1;
    gbc_rdbtnRealTime.gridy = 1;
    add(this.rdbtnRealTime, gbc_rdbtnRealTime);
    ActionListener radioBtnActionListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        timeModel.setDisplayRealTime(TimePanel.this.rdbtnRealTime.isSelected());
        updateTimeFields();
      }
    };
    this.rdbtnRealTime.addActionListener(radioBtnActionListener);

    this.rdbtnModelTime = new JRadioButton("Model Time");
    GridBagConstraints gbc_rdbtnModelTime = new GridBagConstraints();
    gbc_rdbtnModelTime.anchor = GridBagConstraints.WEST;
    gbc_rdbtnModelTime.insets = new Insets(0, 0, 5, 5);
    gbc_rdbtnModelTime.gridx = 1;
    gbc_rdbtnModelTime.gridy = 3;
    add(this.rdbtnModelTime, gbc_rdbtnModelTime);
    this.rdbtnModelTime.addActionListener(radioBtnActionListener);
    this.rdbtnModelTime.setSelected(true);

    JLabel lblStartTime = new JLabel("Start Time");
    GridBagConstraints gbc_lblStartTime = new GridBagConstraints();
    gbc_lblStartTime.insets = new Insets(0, 0, 5, 5);
    gbc_lblStartTime.gridx = 1;
    gbc_lblStartTime.gridy = 5;
    add(lblStartTime, gbc_lblStartTime);

    this.startTimeTextField = new JFormattedTextField(mask);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 0);
    gbc_textField.gridx = 3;
    gbc_textField.gridy = 5;
    add(this.startTimeTextField, gbc_textField);
    this.startTimeTextField.setColumns(10);
    this.startTimeTextField.setValue(timeModel.getTimeSecString(timeModel.getMinTime()));
    this.startTimeTextField.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(FocusEvent arg0) {
        timeModel.setMinTime(timeModel.convertTimeString(TimePanel.this.startTimeTextField.getText()));
      }

      @Override
      public void focusGained(FocusEvent arg0) {
      }
    });

    ButtonGroup timeButtonGroup = new ButtonGroup();
    timeButtonGroup.add(this.rdbtnModelTime);
    timeButtonGroup.add(this.rdbtnRealTime);

    this.lblEndTime = new JLabel("End Time");
    GridBagConstraints gbc_lblEndTime = new GridBagConstraints();
    gbc_lblEndTime.insets = new Insets(0, 0, 0, 5);
    gbc_lblEndTime.gridx = 1;
    gbc_lblEndTime.gridy = 7;
    add(this.lblEndTime, gbc_lblEndTime);

    this.endTimeTextField = new JFormattedTextField(mask);
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.gridx = 3;
    gbc_textField_1.gridy = 7;
    this.endTimeTextField.setColumns(10);
    add(this.endTimeTextField, gbc_textField_1);
    timeModel.setDisplayRealTime(TimePanel.this.rdbtnRealTime.isSelected());
    updateTimeFields();
    this.endTimeTextField.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(FocusEvent arg0) {
        if (timeModel.isDisplayRealTime()) {
          timeModel.setMaxTime(timeModel.convertTimeString(TimePanel.this.endTimeTextField.getText()));
        } else {
          timeModel
              .setMaxTime(timeModel.toRealTime(timeModel.convertTimeString(TimePanel.this.endTimeTextField.getText())));
        }
      }

      @Override
      public void focusGained(FocusEvent arg0) {

      }
    });

  }

}
