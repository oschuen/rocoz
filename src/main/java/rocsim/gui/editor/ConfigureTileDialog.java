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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rocsim.gui.tiles.Tile;

public class ConfigureTileDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  private JTextField nameTextField;
  private JFormattedTextField lengthTextField;
  private JPanel panel;
  private JButton btnOk;
  private JButton btnCancel;

  public ConfigureTileDialog(Tile tile) {
    setModal(true);
    setTitle(tile.getId());
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 10 };
    gridBagLayout.rowHeights = new int[] { 10, 0, 0, 0, 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0 };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0 };
    getContentPane().setLayout(gridBagLayout);

    JLabel lblName = new JLabel("Name");
    GridBagConstraints gbc_lblName = new GridBagConstraints();
    gbc_lblName.insets = new Insets(0, 0, 5, 5);
    gbc_lblName.gridx = 1;
    gbc_lblName.gridy = 1;
    getContentPane().add(lblName, gbc_lblName);

    this.nameTextField = new JTextField();
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 0, 5, 0);
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 3;
    gbc_textField.gridy = 1;
    getContentPane().add(this.nameTextField, gbc_textField);
    this.nameTextField.setText(tile.getId());
    this.nameTextField.setColumns(20);

    JLabel lblLength = new JLabel("Length (1.00 m model-length)");
    GridBagConstraints gbc_lblLength = new GridBagConstraints();
    gbc_lblLength.insets = new Insets(0, 0, 5, 5);
    gbc_lblLength.gridx = 1;
    gbc_lblLength.gridy = 3;
    getContentPane().add(lblLength, gbc_lblLength);

    NumberFormat floatFormatter = NumberFormat.getNumberInstance(Locale.US);
    floatFormatter.setMaximumFractionDigits(2);
    floatFormatter.setGroupingUsed(false);
    this.lengthTextField = new JFormattedTextField(floatFormatter);
    GridBagConstraints gbc_textField_1 = new GridBagConstraints();
    gbc_textField_1.insets = new Insets(0, 0, 5, 0);
    gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField_1.gridx = 3;
    gbc_textField_1.gridy = 3;
    getContentPane().add(this.lengthTextField, gbc_textField_1);
    this.lengthTextField.setColumns(10);
    this.lengthTextField.setValue(tile.getLength());

    this.panel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) this.panel.getLayout();
    flowLayout.setHgap(30);
    GridBagConstraints gbc_panel = new GridBagConstraints();
    gbc_panel.gridwidth = 3;
    gbc_panel.insets = new Insets(0, 0, 0, 5);
    gbc_panel.fill = GridBagConstraints.BOTH;
    gbc_panel.gridx = 1;
    gbc_panel.gridy = 5;
    getContentPane().add(this.panel, gbc_panel);

    this.btnOk = new JButton("Ok");
    this.panel.add(this.btnOk);
    this.btnOk.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        tile.setId(ConfigureTileDialog.this.nameTextField.getText());
        tile.setLength(Float.valueOf(ConfigureTileDialog.this.lengthTextField.getText().replace(',', '.')));
        setVisible(false);
      }
    });

    this.btnCancel = new JButton("CANCEL");
    this.panel.add(this.btnCancel);
    this.btnCancel.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        setVisible(false);
      }
    });
    pack();
  }

}
