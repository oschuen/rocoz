package rocsim.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.TripModel;
import rocsim.schedule.time.TimeModel;

public class TripPanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JTextField idTextField;
  private JTextField commentTextField;
  private JTextField realTimeTextField;
  private JTextField fremoTimeTextField;
  private JComboBox<String> comboBox;
  private TimeModel timeModel;
  private ScheduleFrame panel;

  public TripPanel(TimeModel timeModel) {
    this.timeModel = timeModel;
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);

    JLabel idLabel = new JLabel("ID");
    GridBagConstraints gbc_idLabel = new GridBagConstraints();
    gbc_idLabel.insets = new Insets(0, 0, 5, 5);
    gbc_idLabel.anchor = GridBagConstraints.EAST;
    gbc_idLabel.gridx = 0;
    gbc_idLabel.gridy = 0;
    add(idLabel, gbc_idLabel);

    this.idTextField = new JTextField();
    GridBagConstraints gbc_idTextField = new GridBagConstraints();
    gbc_idTextField.insets = new Insets(0, 0, 5, 5);
    gbc_idTextField.gridx = 1;
    gbc_idTextField.gridy = 0;
    add(this.idTextField, gbc_idTextField);
    this.idTextField.setColumns(10);

    JLabel locoLabel = new JLabel("Loco");
    GridBagConstraints gbc_locoLabel = new GridBagConstraints();
    gbc_locoLabel.insets = new Insets(0, 0, 5, 5);
    gbc_locoLabel.gridx = 2;
    gbc_locoLabel.gridy = 0;
    add(locoLabel, gbc_locoLabel);

    this.comboBox = new JComboBox<>();
    GridBagConstraints gbc_comboBox = new GridBagConstraints();
    gbc_comboBox.insets = new Insets(0, 0, 5, 5);
    gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
    gbc_comboBox.gridx = 3;
    gbc_comboBox.gridy = 0;
    add(this.comboBox, gbc_comboBox);

    JLabel lblStart = new JLabel("Start");
    GridBagConstraints gbc_lblStart = new GridBagConstraints();
    gbc_lblStart.anchor = GridBagConstraints.EAST;
    gbc_lblStart.insets = new Insets(0, 0, 5, 5);
    gbc_lblStart.gridx = 4;
    gbc_lblStart.gridy = 0;
    add(lblStart, gbc_lblStart);

    this.realTimeTextField = new JTextField();
    GridBagConstraints gbc_realTimeTextField = new GridBagConstraints();
    gbc_realTimeTextField.insets = new Insets(0, 0, 5, 5);
    gbc_realTimeTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_realTimeTextField.gridx = 5;
    gbc_realTimeTextField.gridy = 0;
    add(this.realTimeTextField, gbc_realTimeTextField);
    this.realTimeTextField.setColumns(10);

    JLabel lblModelTime = new JLabel("Model Time");
    GridBagConstraints gbc_lblModelTime = new GridBagConstraints();
    gbc_lblModelTime.anchor = GridBagConstraints.EAST;
    gbc_lblModelTime.insets = new Insets(0, 0, 5, 5);
    gbc_lblModelTime.gridx = 6;
    gbc_lblModelTime.gridy = 0;
    add(lblModelTime, gbc_lblModelTime);

    this.fremoTimeTextField = new JTextField();
    GridBagConstraints gbc_fremoTimeTextField = new GridBagConstraints();
    gbc_fremoTimeTextField.insets = new Insets(0, 0, 5, 5);
    gbc_fremoTimeTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_fremoTimeTextField.gridx = 7;
    gbc_fremoTimeTextField.gridy = 0;
    add(this.fremoTimeTextField, gbc_fremoTimeTextField);
    this.fremoTimeTextField.setColumns(10);

    JLabel commentLabel = new JLabel("Comment");
    GridBagConstraints gbc_commentLabel = new GridBagConstraints();
    gbc_commentLabel.insets = new Insets(0, 0, 5, 5);
    gbc_commentLabel.gridx = 8;
    gbc_commentLabel.gridy = 0;
    add(commentLabel, gbc_commentLabel);

    this.commentTextField = new JTextField();
    GridBagConstraints gbc_commentTextField = new GridBagConstraints();
    gbc_commentTextField.insets = new Insets(0, 0, 5, 0);
    gbc_commentTextField.weightx = 2.0;
    gbc_commentTextField.fill = GridBagConstraints.HORIZONTAL;
    gbc_commentTextField.gridx = 9;
    gbc_commentTextField.gridy = 0;
    add(this.commentTextField, gbc_commentTextField);
    this.commentTextField.setColumns(20);

    this.panel = new ScheduleFrame();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.weightx = 10.0;
    gbc_panel.gridwidth = 9;
    gbc_panel.insets = new Insets(0, 0, 0, 5);
    gbc_panel.fill = GridBagConstraints.HORIZONTAL;
    gbc_panel.gridx = 1;
    gbc_panel.gridy = 1;
    add(this.panel, gbc_panel);
  }

  public void setLocoIdComboBoxModel(LocoIdComboBoxModel model) {
    this.comboBox.setModel(model);
  }

  public void setModel(TripModel tripModel) {
    this.idTextField.setText(tripModel.getId());
    this.comboBox.setSelectedItem(tripModel.getLocoId());
    this.realTimeTextField.setText(this.timeModel.getTimeSecString(tripModel.getStartTime()));
    this.fremoTimeTextField.setText(this.timeModel.getFremoTimeSecString(tripModel.getStartTime()));
  }

  public TripModel getModel() {
    TripModel model = new TripModel();
    model.setId(this.idTextField.getText());
    model.setLocoId((String) this.comboBox.getSelectedItem());
    model.setStartTime(this.timeModel.convertTimeString(this.realTimeTextField.getText()));
    return model;
  }

  public void adjustTime() {
    this.fremoTimeTextField.setText(this.timeModel.getFremoTimeSecString(getModel().getStartTime()));
  }

}
