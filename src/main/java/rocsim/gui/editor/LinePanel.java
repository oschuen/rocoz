package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.LineModel;
import rocsim.schedule.model.LineSegmentModel;

public class LinePanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JTextField lineNameTextField;
  private JComboBox<String> startStationComboBox;
  private JPanel innerPanel;
  private LineSegmentFrame segmentFrame;
  private StringListDataModel stationNameDataModel;
  private EditorContext context;

  public LinePanel(EditorContext context) {
    this.context = context;
    this.innerPanel = new JPanel();

    FlowLayout flowLayout = (FlowLayout) this.innerPanel.getLayout();
    flowLayout.setHgap(10);
    flowLayout.setAlignment(FlowLayout.LEFT);

    setLayout(new BorderLayout(5, 5));

    JLabel lblName = new JLabel("Name");
    this.innerPanel.add(lblName);

    this.lineNameTextField = new JTextField();
    this.innerPanel.add(this.lineNameTextField);
    this.lineNameTextField.setColumns(20);

    JLabel lblNewLabel = new JLabel("Start");
    this.innerPanel.add(lblNewLabel);

    this.stationNameDataModel = new StringListDataModel();
    this.stationNameDataModel.setValueList(context.getStationNames());
    this.startStationComboBox = new JComboBox<>();
    this.innerPanel.add(this.startStationComboBox);
    this.stationNameDataModel = new StringListDataModel();
    this.stationNameDataModel.setValueList(context.getStationNames());
    this.startStationComboBox.setModel(new StringComboBoxModel(this.stationNameDataModel));

    add(this.innerPanel, BorderLayout.NORTH);

    this.segmentFrame = new LineSegmentFrame(context);

    add(this.segmentFrame, BorderLayout.CENTER);
  }

  public void setModel(LineModel model) {
    this.lineNameTextField.setText(model.getName());
    if (!model.getLineSegments().isEmpty()) {
      this.startStationComboBox.setSelectedItem(model.getLineSegments().get(0).getStationOne());
    }
    this.segmentFrame.setLineSegmentModels(model.getLineSegments());
  }

  public LineModel getModel() {
    LineModel model = new LineModel();
    model.setName(this.lineNameTextField.getText());
    String stationK1 = (String) this.startStationComboBox.getSelectedItem();
    for (LineSegmentModel segmentModel : this.segmentFrame.getLineSegmentModels()) {
      segmentModel.setStationOne(stationK1);
      model.addSegment(segmentModel);
      stationK1 = segmentModel.getStationTwo();
    }
    return model;
  }

  public void updateContext() {
    this.segmentFrame.updateContext();
    this.stationNameDataModel.setValueList(this.context.getStationNames());
  }

}
