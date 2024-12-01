package rocsim.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.ScheduleModel;

public class SchedulePanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JFormattedTextField timeTextField;
  private JTextField commentTextField;
  private JFormattedTextField pauseTextField;
  private JComboBox<String> startBlockComboBox;
  private JComboBox<String> endBlockComboBox;

  public SchedulePanel(StringListDataModel blockIdDataModel) {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);

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

    JLabel lblNewLabel_1 = new JLabel("Time (min)");
    GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
    gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_1.gridx = 4;
    gbc_lblNewLabel_1.gridy = 0;
    add(lblNewLabel_1, gbc_lblNewLabel_1);

    this.timeTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    GridBagConstraints gbc_timeTextField = new GridBagConstraints();
    gbc_timeTextField.insets = new Insets(0, 0, 5, 5);
    gbc_timeTextField.gridx = 5;
    gbc_timeTextField.gridy = 0;
    add(this.timeTextField, gbc_timeTextField);
    this.timeTextField.setColumns(10);

    JLabel lblNewLabel_3 = new JLabel("Pause (min)");
    GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
    gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
    gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel_3.gridx = 6;
    gbc_lblNewLabel_3.gridy = 0;
    add(lblNewLabel_3, gbc_lblNewLabel_3);

    this.pauseTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    GridBagConstraints gbc_pauseTextField = new GridBagConstraints();
    gbc_pauseTextField.insets = new Insets(0, 0, 0, 5);
    gbc_pauseTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_pauseTextField.gridx = 7;
    gbc_pauseTextField.gridy = 0;
    add(this.pauseTextField, gbc_pauseTextField);
    this.pauseTextField.setColumns(10);

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
    this.pauseTextField.setValue(scheduleModel.getPause());
    this.timeTextField.setValue(scheduleModel.getDuration());
    this.commentTextField.setText(scheduleModel.getComment());
  }
}
