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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;
import rocsim.track.Block;

public class SchedulePanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JFormattedTextField drivingTimeTextField;
  private JFormattedTextField pauseTextField;
  private JTextField commentTextField;
  private JComboBox<String> platformComboBox;
  private JComboBox<String> stationComboBox;
  private int pauseInRealTime = 0;
  private int durationInRealTime = 0;
  private EditorContext context;
  private StringListDataModel stationNameDataModel;
  private StringListDataModel platformDataModel;
  private String previousBlockId = "";

  public SchedulePanel(EditorContext context) {
    this.context = context;

    MaskFormatter mask = null;
    try {
      mask = new MaskFormatter("##:##:##");
      mask.setPlaceholderCharacter('#');
    } catch (ParseException e) {
    }

    context.getTimeModel().addListener(new TimeModelChangeListener() {
      @Override
      public void timeModelChanged() {
        updateTime();
      }
    });

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
        Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
    setLayout(gridBagLayout);

    JLabel lblStation = new JLabel("Station");
    GridBagConstraints gbc_lblStation = new GridBagConstraints();
    gbc_lblStation.anchor = GridBagConstraints.WEST;
    gbc_lblStation.insets = new Insets(0, 0, 5, 5);
    gbc_lblStation.gridx = 0;
    gbc_lblStation.gridy = 0;
    add(lblStation, gbc_lblStation);

    this.stationComboBox = new JComboBox<>();
    GridBagConstraints gbc_stationComboBox = new GridBagConstraints();
    gbc_stationComboBox.anchor = GridBagConstraints.WEST;
    gbc_stationComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_stationComboBox.gridx = 1;
    gbc_stationComboBox.gridy = 0;
    add(this.stationComboBox, gbc_stationComboBox);
    this.stationNameDataModel = new StringListDataModel();
    this.stationNameDataModel.setValueList(context.getStationNames());
    this.stationComboBox.setModel(new StringComboBoxModel(this.stationNameDataModel));
    this.stationComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        String selectedStation = (String) SchedulePanel.this.stationComboBox.getSelectedItem();
        if (!(selectedStation == null || selectedStation.isBlank())) {
          SchedulePanel.this.platformDataModel
              .setValueList(context.getPlatforms((String) SchedulePanel.this.stationComboBox.getSelectedItem()));
        }
        fireDataChanged();
      }
    });

    JLabel platformLabel = new JLabel("Platform");
    GridBagConstraints gbc_platformLabel = new GridBagConstraints();
    gbc_platformLabel.anchor = GridBagConstraints.WEST;
    gbc_platformLabel.insets = new Insets(0, 0, 5, 5);
    gbc_platformLabel.gridx = 2;
    gbc_platformLabel.gridy = 0;
    add(platformLabel, gbc_platformLabel);

    this.platformComboBox = new JComboBox<>();
    GridBagConstraints gbc_platformComboBox = new GridBagConstraints();
    gbc_platformComboBox.anchor = GridBagConstraints.WEST;
    gbc_platformComboBox.insets = new Insets(0, 0, 5, 5);
    gbc_platformComboBox.gridx = 3;
    gbc_platformComboBox.gridy = 0;
    add(this.platformComboBox, gbc_platformComboBox);
    this.platformDataModel = new StringListDataModel();
    this.platformDataModel.setValueList(context.getPlatforms((String) this.stationComboBox.getSelectedItem()));
    this.platformComboBox.setModel(new StringComboBoxModel(this.platformDataModel));
    this.platformComboBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        fireDataChanged();
      }
    });

    JButton calcButton = new JButton("Calculate");
    GridBagConstraints gbc_Button = new GridBagConstraints();
    gbc_Button.anchor = GridBagConstraints.WEST;
    gbc_Button.insets = new Insets(0, 0, 5, 5);
    gbc_Button.gridx = 4;
    gbc_Button.gridy = 0;
    add(calcButton, gbc_Button);
    calcButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        calcDuration();
      }
    });

    JLabel lblNewLabel = new JLabel("Driving Time");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 5;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    this.drivingTimeTextField = new JFormattedTextField(mask);
    this.drivingTimeTextField.setValue("1");
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.anchor = GridBagConstraints.WEST;
    gbc_textField.insets = new Insets(0, 0, 5, 0);
    gbc_textField.gridx = 6;
    gbc_textField.gridy = 0;
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    add(this.drivingTimeTextField, gbc_textField);
    this.drivingTimeTextField.setColumns(7);
    this.drivingTimeTextField.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(FocusEvent arg0) {
        int drivingTime = context.getTimeModel().convertTimeString(SchedulePanel.this.drivingTimeTextField.getText());
        if (context.getTimeModel().isDisplayRealTime()) {
          SchedulePanel.this.durationInRealTime = drivingTime;
        } else {
          SchedulePanel.this.durationInRealTime = drivingTime / context.getTimeModel().getRadix();
        }
      }

      @Override
      public void focusGained(FocusEvent arg0) {
      }
    });

    JLabel lblNewLabel_1 = new JLabel("Pause");
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_1.gridx = 7;
    gbc_lblNewLabel_1.gridy = 0;
    add(lblNewLabel_1, gbc_lblNewLabel_1);

    this.pauseTextField = new JFormattedTextField(mask);
    this.pauseTextField.setValue("0");
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.anchor = GridBagConstraints.WEST;
    gbc_textField_1.insets = new Insets(0, 0, 5, 5);
    gbc_textField_1.gridx = 8;
    gbc_textField_1.gridy = 0;
    gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
    add(this.pauseTextField, gbc_textField_1);
    this.pauseTextField.setColumns(7);
    this.pauseTextField.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(FocusEvent arg0) {
        int pauseTime = context.getTimeModel().convertTimeString(SchedulePanel.this.pauseTextField.getText());
        if (context.getTimeModel().isDisplayRealTime()) {
          SchedulePanel.this.pauseInRealTime = pauseTime;
        } else {
          SchedulePanel.this.pauseInRealTime = pauseTime / context.getTimeModel().getRadix();
        }
      }

      @Override
      public void focusGained(FocusEvent arg0) {
      }
    });

    JLabel lblComment = new JLabel("Comment");
    GridBagConstraints gbc_lblComment = new GridBagConstraints();
    gbc_lblComment.anchor = GridBagConstraints.WEST;
    gbc_lblComment.insets = new Insets(0, 0, 5, 5);
    gbc_lblComment.gridx = 9;
    gbc_lblComment.gridy = 0;
    add(lblComment, gbc_lblComment);

    this.commentTextField = new JTextField();
    GridBagConstraints gbc_textField_2 = new GridBagConstraints();
    gbc_textField_2.anchor = GridBagConstraints.WEST;
    gbc_textField_2.gridx = 10;
    gbc_textField_2.gridy = 0;
    gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
    add(this.commentTextField, gbc_textField_2);
    this.commentTextField.setColumns(20);
  }

  private void updateTime() {
    if (this.context.getTimeModel().isDisplayRealTime()) {
      this.drivingTimeTextField.setValue(this.context.getTimeModel().getTimeSecString(this.durationInRealTime));
      this.pauseTextField.setValue(this.context.getTimeModel().getTimeSecString(this.pauseInRealTime));
    } else {
      this.drivingTimeTextField.setValue(this.context.getTimeModel()
          .getTimeSecString(this.durationInRealTime * this.context.getTimeModel().getRadix()));
      this.pauseTextField.setValue(
          this.context.getTimeModel().getTimeSecString(this.pauseInRealTime * this.context.getTimeModel().getRadix()));
    }
  }

  /**
   * @param previousBlockId the previousBlockId to set
   */
  public void setPreviousBlockId(String previousBlockId) {
    this.previousBlockId = previousBlockId;
  }

  public String getBlockId() {
    return this.context.getBlockForPlatform((String) this.stationComboBox.getSelectedItem(),
        (String) this.platformComboBox.getSelectedItem());
  }

  public ScheduleModel getModel() {
    ScheduleModel scheduleModel = new ScheduleModel();
    scheduleModel.setPause(this.pauseInRealTime);
    scheduleModel.setDuration(this.durationInRealTime);
    scheduleModel.setComment(this.commentTextField.getText());
    scheduleModel.setStartBlock(this.previousBlockId);
    scheduleModel.setEndBlock(getBlockId());

    return scheduleModel;
  }

  public void setModel(ScheduleModel scheduleModel) {
    Entry<String, String> stationAndPlatform = this.context.getStationAndPlatform(scheduleModel.getEndBlock());
    this.stationComboBox.setSelectedItem(stationAndPlatform.getKey());
    this.platformComboBox.setSelectedItem(stationAndPlatform.getValue());
    this.pauseInRealTime = scheduleModel.getPause();
    this.durationInRealTime = scheduleModel.getDuration();
    this.commentTextField.setText(scheduleModel.getComment());
    updateTime();
  }

  private void calcDuration() {
    Block block = this.context.getBlock(this.previousBlockId, getBlockId());
    this.durationInRealTime = block.getDriveTime(60);
    updateTime();
  }

  public void updateContext() {
    this.stationNameDataModel.setValueList(this.context.getStationNames());

  }
}
