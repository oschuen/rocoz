package rocsim.gui.editor;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.PlatformModel;

public class PlatformPanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JTextField platformNameTextField;
  private JComboBox<String> blockComboBox;
  private StringListDataModel blockIdDataModel;
  private StringListDataModel stationNameDataModel;
  private EditorContext context;

  public PlatformPanel(EditorContext context) {
    this.context = context;
    this.blockIdDataModel = new StringListDataModel();
    this.blockIdDataModel.setValueList(context.getArrivableBlockIds());
    FlowLayout flowLayout = (FlowLayout) getLayout();
    flowLayout.setHgap(10);
    flowLayout.setVgap(2);
    flowLayout.setAlignment(FlowLayout.LEFT);

    JLabel lblName = new JLabel("Platform Name");
    add(lblName);

    this.platformNameTextField = new JTextField();
    add(this.platformNameTextField);
    this.platformNameTextField.setColumns(10);

    JLabel lblNewLabel = new JLabel("Block");
    add(lblNewLabel);

    this.blockComboBox = new JComboBox<>();
    add(this.blockComboBox);
    this.blockComboBox.setModel(new StringComboBoxModel(this.blockIdDataModel));
  }

  public void setModel(PlatformModel model) {
    this.platformNameTextField.setText(model.getName());
    this.blockComboBox.setSelectedItem(model.getBlockId());
  }

  public PlatformModel getModel() {
    PlatformModel model = new PlatformModel();
    model.setName(this.platformNameTextField.getText());
    model.setBlockId((String) this.blockComboBox.getSelectedItem());
    return model;
  }

  public void updateContext() {
    this.blockIdDataModel.setValueList(this.context.getArrivableBlockIds());
  }
}
