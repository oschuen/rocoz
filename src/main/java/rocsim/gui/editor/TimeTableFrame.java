package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;

public class TimeTableFrame extends JPanel {
  private StringListDataModel lineModel;
  private EditorContext context;
  private JScrollBar timeScrollBar;
  private JComboBox<String> comboBox;

  public TimeTableFrame(EditorContext context) {
    setLayout(new BorderLayout(0, 0));
    this.context = context;

    JPanel panel = new JPanel();
    add(panel, BorderLayout.NORTH);

    this.comboBox = new JComboBox<>();
    panel.add(this.comboBox);
    this.lineModel = new StringListDataModel();
    this.comboBox.setModel(new StringComboBoxModel(this.lineModel));
    this.lineModel.setValueList(context.getLineNames());

    JScrollBar scrollBar = new JScrollBar();
    scrollBar.setOrientation(JScrollBar.HORIZONTAL);
    add(scrollBar, BorderLayout.SOUTH);

    this.timeScrollBar = new JScrollBar();
    add(this.timeScrollBar, BorderLayout.EAST);

    TimeTablePanel timeTablePanel = new TimeTablePanel(context);
    add(timeTablePanel, BorderLayout.CENTER);

    this.comboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        timeTablePanel.setLine((String) TimeTableFrame.this.comboBox.getSelectedItem());
      }
    });
    this.timeScrollBar.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent arg0) {
        timeTablePanel.setTopTime(arg0.getValue() * 60);

      }
    });
  }

  public void updateContext() {
    this.lineModel.setValueList(this.context.getLineNames());
    this.timeScrollBar.setMinimum(this.context.getTimeModel().getMinTime() / 60 - 20);
    this.timeScrollBar.setMaximum(this.context.getTimeModel().getMaxTime() / 60 + 20);
    if (this.comboBox.getSelectedIndex() < 0) {
      this.comboBox.setSelectedIndex(0);
    }
  }
}
