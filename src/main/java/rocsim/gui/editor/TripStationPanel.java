package rocsim.gui.editor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.MaskFormatter;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;
import rocsim.schedule.model.TripModel;

public class TripStationPanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JTextField idTextField;
  private JFormattedTextField timeTextField;
  private JTextField commentTextField;
  private JComboBox<String> locoComboBox;
  private JComboBox<String> platformComboBox;
  private JComboBox<String> stationComboBox;
  private ScheduleFrame scheduleFrame;
  private EditorContext context;
  private StringListDataModel locoModel;
  private StringListDataModel stationNameDataModel;
  private StringListDataModel platformDataModel;
  private int startInRealTime = 0;

  public TripStationPanel(EditorContext context) {
    this.context = context;
    MaskFormatter mask = null;
    try {
      mask = new MaskFormatter("##:##:##");
      mask.setPlaceholderCharacter('#');
    } catch (ParseException e) {
    }
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);

    JLabel lblId = new JLabel("ID");
    GridBagConstraints gbc_lblId = new GridBagConstraints();
    gbc_lblId.insets = new Insets(0, 0, 5, 5);
    gbc_lblId.anchor = GridBagConstraints.WEST;
    gbc_lblId.gridx = 0;
    gbc_lblId.gridy = 0;
    add(lblId, gbc_lblId);

    this.idTextField = new JTextField();
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 5);
    gbc_textField.anchor = GridBagConstraints.WEST;
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 0;
    add(this.idTextField, gbc_textField);
    this.idTextField.setColumns(10);

    JLabel lblNewLabel = new JLabel("Loco");
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 2;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    this.locoComboBox = new JComboBox<>();
    GridBagConstraints gbc_comboBox = new GridBagConstraints();
    gbc_comboBox.insets = new Insets(0, 0, 5, 5);
    gbc_comboBox.gridx = 3;
    gbc_comboBox.gridy = 0;
    add(this.locoComboBox, gbc_comboBox);
    this.locoModel = new StringListDataModel();
    this.locoModel.setValueList(context.getLocoIds());
    this.locoComboBox.setModel(new StringComboBoxModel(this.locoModel));

    JLabel lblStation = new JLabel("Station");
    GridBagConstraints gbc_lblStation = new GridBagConstraints();
    gbc_lblStation.anchor = GridBagConstraints.WEST;
    gbc_lblStation.insets = new Insets(0, 0, 5, 5);
    gbc_lblStation.gridx = 5;
    gbc_lblStation.gridy = 0;
    add(lblStation, gbc_lblStation);

    this.stationComboBox = new JComboBox<>();
    GridBagConstraints gbc_comboBox_1 = new GridBagConstraints();
    gbc_comboBox_1.insets = new Insets(0, 0, 5, 5);
    gbc_comboBox_1.fill = GridBagConstraints.NONE;
    gbc_comboBox_1.gridx = 6;
    gbc_comboBox_1.gridy = 0;
    add(this.stationComboBox, gbc_comboBox_1);
    this.stationNameDataModel = new StringListDataModel();
    this.stationNameDataModel.setValueList(context.getStationNames());
    this.stationComboBox.setModel(new StringComboBoxModel(this.stationNameDataModel));
    this.stationComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        String selectedStation = (String) TripStationPanel.this.stationComboBox.getSelectedItem();
        if (!(selectedStation == null || selectedStation.isBlank())) {
          TripStationPanel.this.platformDataModel
              .setValueList(context.getPlatforms((String) TripStationPanel.this.stationComboBox.getSelectedItem()));
        }
        fireDataChanged();
      }
    });

    JLabel lblPlatform = new JLabel("Platform");
    GridBagConstraints gbc_lblPlatform = new GridBagConstraints();
    gbc_lblPlatform.anchor = GridBagConstraints.EAST;
    gbc_lblPlatform.insets = new Insets(0, 0, 5, 5);
    gbc_lblPlatform.gridx = 7;
    gbc_lblPlatform.gridy = 0;
    add(lblPlatform, gbc_lblPlatform);

    this.platformComboBox = new JComboBox<>();
    GridBagConstraints gbc_comboBox_2 = new GridBagConstraints();
    gbc_comboBox_2.insets = new Insets(0, 0, 5, 5);
    gbc_comboBox_2.fill = GridBagConstraints.NONE;
    gbc_comboBox_2.gridx = 8;
    gbc_comboBox_2.gridy = 0;
    add(this.platformComboBox, gbc_comboBox_2);
    this.platformDataModel = new StringListDataModel();
    this.platformDataModel.setValueList(context.getPlatforms((String) this.stationComboBox.getSelectedItem()));
    this.platformComboBox.setModel(new StringComboBoxModel(this.platformDataModel));
    this.platformComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        fireDataChanged();
      }
    });

    JLabel lblTime = new JLabel("Time");
    GridBagConstraints gbc_lblTime = new GridBagConstraints();
    gbc_lblTime.anchor = GridBagConstraints.WEST;
    gbc_lblTime.insets = new Insets(0, 0, 5, 5);
    gbc_lblTime.gridx = 9;
    gbc_lblTime.gridy = 0;
    add(lblTime, gbc_lblTime);

    this.timeTextField = new JFormattedTextField(mask);
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.insets = new Insets(0, 0, 5, 5);
    gbc_textField_1.fill = GridBagConstraints.NONE;
    gbc_textField_1.gridx = 10;
    gbc_textField_1.gridy = 0;
    add(this.timeTextField, gbc_textField_1);
    this.timeTextField.setColumns(10);
    this.timeTextField.addFocusListener(new FocusListener() {

      @Override
      public void focusLost(FocusEvent arg0) {
        int startTime = context.getTimeModel().convertTimeString(TripStationPanel.this.timeTextField.getText());
        if (context.getTimeModel().isDisplayRealTime()) {
          TripStationPanel.this.startInRealTime = startTime;
        } else {
          TripStationPanel.this.startInRealTime = context.getTimeModel().toRealTime(startTime);
        }
      }

      @Override
      public void focusGained(FocusEvent arg0) {
      }
    });
    updateTime();

    JLabel lblComent = new JLabel("Comment");
    GridBagConstraints gbc_lblComent = new GridBagConstraints();
    gbc_lblComent.anchor = GridBagConstraints.WEST;
    gbc_lblComent.fill = GridBagConstraints.NONE;
    gbc_lblComent.insets = new Insets(0, 0, 5, 5);
    gbc_lblComent.gridx = 11;
    gbc_lblComent.gridy = 0;
    add(lblComent, gbc_lblComent);

    this.commentTextField = new JTextField();
    GridBagConstraints gbc_textField_2 = new GridBagConstraints();
    gbc_textField_2.insets = new Insets(0, 0, 5, 5);
    gbc_textField_2.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_2.gridx = 12;
    gbc_textField_2.gridy = 0;
    gbc_textField_2.weightx = 20.0;
    gbc_textField_2.anchor = GridBagConstraints.WEST;

    add(this.commentTextField, gbc_textField_2);
    this.commentTextField.setColumns(40);

    this.scheduleFrame = new ScheduleFrame(context);
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.weightx = 10.0;
    gbc_panel.gridwidth = 12;
    gbc_panel.insets = new Insets(0, 0, 0, 5);
    gbc_panel.fill = GridBagConstraints.HORIZONTAL;
    gbc_panel.gridx = 1;
    gbc_panel.gridy = 1;
    add(this.scheduleFrame, gbc_panel);

    context.getTimeModel().addListener(new TimeModelChangeListener() {
      @Override
      public void timeModelChanged() {
        updateTime();
      }
    });
    ListDataListener dataListener = new ListDataListener() {

      @Override
      public void intervalRemoved(ListDataEvent arg0) {
        contentsChanged(arg0);

      }

      @Override
      public void intervalAdded(ListDataEvent arg0) {
        contentsChanged(arg0);

      }

      @Override
      public void contentsChanged(ListDataEvent arg0) {
        updatePreviousBlockIds();
      }
    };
    this.scheduleFrame.addDataListener(dataListener);
    this.addDataListener(dataListener);
  }

  private void updateTime() {
    if (this.context.getTimeModel().isDisplayRealTime()) {
      this.timeTextField.setValue(this.context.getTimeModel().getTimeSecString(this.startInRealTime));
    } else {
      this.timeTextField.setValue(this.context.getTimeModel().getFremoTimeSecString(this.startInRealTime));
    }
  }

  public void setModel(TripModel tripModel) {
    this.idTextField.setText(tripModel.getId());
    this.locoComboBox.setSelectedItem(tripModel.getLocoId());
    this.stationComboBox.setSelectedItem(tripModel.getStation());
    this.platformComboBox.setSelectedItem(tripModel.getPlatform());
    this.startInRealTime = tripModel.getStartTime();
    this.scheduleFrame.setScheduleModels(tripModel.getSchedules());
  }

  public TripModel getModel() {
    TripModel model = new TripModel();
    model.setId(this.idTextField.getText());
    model.setLocoId((String) this.locoComboBox.getSelectedItem());
    model.setStartTime(this.startInRealTime);
    model.setStation((String) this.stationComboBox.getSelectedItem());
    model.setPlatform((String) this.platformComboBox.getSelectedItem());
    for (ScheduleModel scheduleModel : this.scheduleFrame.getScheduleModels()) {
      model.addSchedule(scheduleModel);
    }
    return model;
  }

  private void updatePreviousBlockIds() {
    String myBlockIdK1 = this.context.getBlockForPlatform((String) this.stationComboBox.getSelectedItem(),
        (String) this.platformComboBox.getSelectedItem());
    this.scheduleFrame.updatePreviousBlockIds(myBlockIdK1);
  }

  public void updateContext() {
    this.scheduleFrame.updateContext();
    this.locoModel.setValueList(this.context.getLocoIds());
    this.stationNameDataModel.setValueList(this.context.getStationNames());
    updatePreviousBlockIds();
  }
}
