package rocsim.gui.editor;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LinePanel extends JPanel {
  private JTextField lineNameTextField;

  public LinePanel() {
    FlowLayout flowLayout = (FlowLayout) getLayout();
    flowLayout.setHgap(10);
    flowLayout.setAlignment(FlowLayout.LEFT);

    JLabel lblName = new JLabel("Name");
    add(lblName);

    this.lineNameTextField = new JTextField();
    add(this.lineNameTextField);
    this.lineNameTextField.setColumns(30);
  }

}
