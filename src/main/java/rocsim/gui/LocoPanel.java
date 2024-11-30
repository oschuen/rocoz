package rocsim.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LocoPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private JTextField idTextField;
  private JTextField vMaxTextField;

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
    gbc_lblNewLabel.weightx = 1.0;
    gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
    gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);

    this.idTextField = new JTextField();
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.weightx = 1.0;
    gbc_textField.anchor = GridBagConstraints.WEST;
    gbc_textField.insets = new Insets(5, 5, 5, 5);
    gbc_textField.gridx = 1;
    gbc_textField.gridy = 0;
    add(this.idTextField, gbc_textField);
    this.idTextField.setColumns(20);

    JLabel vMaxPanel = new JLabel("V Max");
    GridBagConstraints gbc_Comment = new GridBagConstraints();
    gbc_Comment.weightx = 1.0;
    gbc_Comment.anchor = GridBagConstraints.WEST;
    gbc_Comment.insets = new Insets(5, 5, 5, 5);
    gbc_Comment.gridx = 2;
    gbc_Comment.gridy = 0;
    add(vMaxPanel, gbc_Comment);

    this.vMaxTextField = new JTextField();
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.weightx = 1.0;
    gbc_textField_1.insets = new Insets(5, 5, 5, 5);
    gbc_textField_1.anchor = GridBagConstraints.WEST;
    gbc_textField_1.gridwidth = 3;
    gbc_textField_1.gridx = 3;
    gbc_textField_1.gridy = 0;
    add(this.vMaxTextField, gbc_textField_1);
    this.vMaxTextField.setColumns(10);

    JPanel fill = new JPanel();
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.fill = GridBagConstraints.HORIZONTAL;
    gbc_panel.weightx = 10.0;
    gbc_panel.insets = new Insets(5, 5, 5, 5);
    gbc_panel.anchor = GridBagConstraints.WEST;
    gbc_panel.gridwidth = 3;
    gbc_panel.gridx = 4;
    gbc_panel.gridy = 0;
    add(fill, gbc_panel);
  }
}
