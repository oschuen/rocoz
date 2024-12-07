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
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel;

public class SchedulePanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JFormattedTextField timeTextField;
  private JTextField commentTextField;
  private JFormattedTextField pauseTextField;
  private JComboBox<String> startBlockComboBox;
  private JComboBox<String> endBlockComboBox;
  private TimeModel timeModel;

  public SchedulePanel(TimeModel timeModel, StringListDataModel blockIdDataModel) {
    this.timeModel = timeModel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);

    MaskFormatter mask = null;
    try {
      mask = new MaskFormatter("##:##:##");
      mask.setPlaceholderCharacter('#');
    } catch (ParseException e) {
    }

    JLabel lblStart = new JLabel("Start");
    GridBagConstraints gbc_lblStart = new GridBagConstraints();
    gbc_lblStart.insets = new Insets(0, 0, 5, 5);
    gbc_lblStart.anchor = GridBagConstraints.EAST;
    gbc_lblStart.gridx = 0;
    gbc_lblStart.gridy = 0;
    add(lblStart, gbc_lblStart);

    this.startBlockComboBox = new JComboBox<>();
    this.startBlockComboBox.setModel(new StringComboBoxModel(blockIdDataModel));
    GridBagConstraints gbc_startBlockComboBox = new GridBagConstraints();
    gbc_startBlockComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_startBlockComboBox.gridx = 1;
    gbc_startBlockComboBox.gridy = 0;
    add(this.startBlockComboBox, gbc_startBlockComboBox);

    JLabel lblNewLabel = new JLabel("Destination");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 2;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    this.endBlockComboBox = new JComboBox<>();
    this.endBlockComboBox.setModel(new StringComboBoxModel(blockIdDataModel));
    GridBagConstraints gbc_endBlockComboBox = new GridBagConstraints();
    gbc_endBlockComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_endBlockComboBox.gridx = 3;
    gbc_endBlockComboBox.gridy = 0;
    add(this.endBlockComboBox, gbc_endBlockComboBox);

    JLabel lblNewLabel_1 = new JLabel("Time (hh:mm:ss)");
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_1.gridx = 4;
    gbc_lblNewLabel_1.gridy = 0;
    add(lblNewLabel_1, gbc_lblNewLabel_1);

    this.timeTextField = new JFormattedTextField(mask);
    GridBagConstraints gbc_timeTextField = new GridBagConstraints();
    gbc_timeTextField.insets = new Insets(0, 0, 5, 5);
    gbc_timeTextField.gridx = 5;
    gbc_timeTextField.gridy = 0;
    add(this.timeTextField, gbc_timeTextField);
    this.timeTextField.setColumns(8);

    JLabel lblNewLabel_3 = new JLabel("Pause (hh:mm:ss)");
    GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
    gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_3.gridx = 6;
    gbc_lblNewLabel_3.gridy = 0;
    add(lblNewLabel_3, gbc_lblNewLabel_3);

    this.pauseTextField = new JFormattedTextField(mask);
    GridBagConstraints gbc_pauseTextField = new GridBagConstraints();
    gbc_pauseTextField.insets = new Insets(0, 0, 0, 5);
    gbc_pauseTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_pauseTextField.gridx = 7;
    gbc_pauseTextField.gridy = 0;
    add(this.pauseTextField, gbc_pauseTextField);
    this.pauseTextField.setColumns(8);

    JLabel lblNewLabel_2 = new JLabel("Comment");
    GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
    gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_2.gridx = 8;
    gbc_lblNewLabel_2.gridy = 0;
    add(lblNewLabel_2, gbc_lblNewLabel_2);

    this.commentTextField = new JTextField();
    GridBagConstraints gbc_commentTextField = new GridBagConstraints();
    gbc_commentTextField.insets = new Insets(0, 0, 5, 0);
    gbc_commentTextField.weightx = 10.0;
    gbc_commentTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_commentTextField.gridx = 9;
    gbc_commentTextField.gridy = 0;
    add(this.commentTextField, gbc_commentTextField);
    this.commentTextField.setColumns(20);
  }

  public void setModel(ScheduleModel scheduleModel) {
    this.startBlockComboBox.setSelectedItem(scheduleModel.getStartBlock());
    this.endBlockComboBox.setSelectedItem(scheduleModel.getEndBlock());
    this.pauseTextField.setValue(this.timeModel.getTimeSecString(scheduleModel.getPause()));
    this.timeTextField.setValue(this.timeModel.getTimeSecString(scheduleModel.getDuration()));
    this.commentTextField.setText(scheduleModel.getComment());
  }

  public ScheduleModel getModel() {
    ScheduleModel model = new ScheduleModel();
    model.setStartBlock((String) this.startBlockComboBox.getSelectedItem());
    model.setEndBlock((String) this.endBlockComboBox.getSelectedItem());
    model.setPause(this.timeModel.convertTimeString(this.pauseTextField.getText()));
    model.setDuration(this.timeModel.convertTimeString(this.timeTextField.getText()));
    model.setComment(this.commentTextField.getText());

    return model;
  }
}
