package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;

public class TimeTableFrame extends JPanel {
  private static final long serialVersionUID = 1L;
  private StringListDataModel lineModel;
  private StringListDataModel locoModel;
  private EditorContext context;
  private JScrollBar timeScrollBar;
  private JComboBox<String> lineComboBox;
  private JComboBox<String> locoComboBox;
  private JCheckBox shuntingBox;
  private TimeTablePanel timeTablePanel;

  public TimeTableFrame(EditorContext context) {
    setLayout(new BorderLayout(0, 0));
    this.context = context;

    JPanel panel = new JPanel();
    add(panel, BorderLayout.NORTH);

    panel.add(new JLabel("Line"));
    this.lineComboBox = new JComboBox<>();
    panel.add(this.lineComboBox);
    this.lineModel = new StringListDataModel();
    this.lineComboBox.setModel(new StringComboBoxModel(this.lineModel));
    this.lineModel.setValueList(context.getLineNames());
    panel.add(new JLabel("Loco"));
    this.locoComboBox = new JComboBox<>();
    panel.add(this.locoComboBox);
    this.locoModel = new StringListDataModel();
    this.locoComboBox.setModel(new StringComboBoxModel(this.locoModel));
    this.shuntingBox = new JCheckBox("Shunting");
    panel.add(this.shuntingBox);

    JScrollBar scrollBar = new JScrollBar();
    scrollBar.setOrientation(JScrollBar.HORIZONTAL);
    add(scrollBar, BorderLayout.SOUTH);

    this.timeScrollBar = new JScrollBar();
    add(this.timeScrollBar, BorderLayout.EAST);

    this.timeTablePanel = new TimeTablePanel(context);
    add(this.timeTablePanel, BorderLayout.CENTER);

    this.lineComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        TimeTableFrame.this.timeTablePanel.setLine((String) TimeTableFrame.this.lineComboBox.getSelectedItem());
      }
    });
    this.locoComboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        TimeTableFrame.this.timeTablePanel.setLoco((String) TimeTableFrame.this.locoComboBox.getSelectedItem());
      }
    });

    this.timeScrollBar.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent arg0) {
        TimeTableFrame.this.timeTablePanel.setTopTime(arg0.getValue() * 60);
      }
    });
    this.shuntingBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        TimeTableFrame.this.timeTablePanel.setShuntingView(TimeTableFrame.this.shuntingBox.isSelected());
      }
    });
  }

  public void updateContext() {
    this.lineModel.setValueList(this.context.getLineNames());
    this.locoModel.setValueList(this.context.getLocoIds());
    this.timeScrollBar.setMinimum(this.context.getTimeModel().getMinTime() / 60 - 20);
    this.timeScrollBar.setMaximum(this.context.getTimeModel().getMaxTime() / 60 + 20);
    if (this.lineComboBox.getSelectedIndex() < 0) {
      this.lineComboBox.setSelectedIndex(0);
    }
    if (this.locoComboBox.getSelectedIndex() < 0) {
      this.locoComboBox.setSelectedIndex(0);
    }
    this.timeTablePanel.setLine((String) TimeTableFrame.this.lineComboBox.getSelectedItem());
  }
}
