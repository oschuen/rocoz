package rocsim.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;

public class ControlPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private JScrollBar scrollBar;
  private ClockPanel clock;
  private JRadioButton rdbtnNewRadioButton;
  private JRadioButton rdbtnNewRadioButton_1;
  private JRadioButton rdbtnNewRadioButton_2;
  private JRadioButton rdbtnNewRadioButton_3;
  private JRadioButton rdbtnNewRadioButton_4;
  private JRadioButton rdbtnNewRadioButton_5;
  private ButtonGroup group = new ButtonGroup();
  private int increment = 1;

  public ControlPanel() {
    setLayout(new BorderLayout(10, 10));

    this.clock = new ClockPanel(0);
    add(this.clock, BorderLayout.WEST);

    this.scrollBar = new JScrollBar();
    add(this.scrollBar, BorderLayout.SOUTH);
    this.scrollBar.setOrientation(JScrollBar.HORIZONTAL);

    JPanel panel = new JPanel();
    add(panel, BorderLayout.CENTER);
    panel.setLayout(new GridLayout(3, 5, 0, 0));

    this.rdbtnNewRadioButton = new JRadioButton("1");
    this.rdbtnNewRadioButton.setSelected(true);
    this.rdbtnNewRadioButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.rdbtnNewRadioButton.isSelected()) {
          ControlPanel.this.increment = 1;
        }
      }
    });
    panel.add(this.rdbtnNewRadioButton);

    this.rdbtnNewRadioButton_1 = new JRadioButton("2");
    this.rdbtnNewRadioButton_1.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.rdbtnNewRadioButton_1.isSelected()) {
          ControlPanel.this.increment = 2;
        }
      }
    });

    panel.add(this.rdbtnNewRadioButton_1);

    this.rdbtnNewRadioButton_2 = new JRadioButton("3");
    this.rdbtnNewRadioButton_2.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.rdbtnNewRadioButton_2.isSelected()) {
          ControlPanel.this.increment = 3;
        }
      }
    });

    panel.add(this.rdbtnNewRadioButton_2);

    this.rdbtnNewRadioButton_3 = new JRadioButton("4");
    this.rdbtnNewRadioButton_3.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.rdbtnNewRadioButton_3.isSelected()) {
          ControlPanel.this.increment = 4;
        }
      }
    });

    panel.add(this.rdbtnNewRadioButton_3);

    this.rdbtnNewRadioButton_4 = new JRadioButton("5");
    this.rdbtnNewRadioButton_4.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.rdbtnNewRadioButton_4.isSelected()) {
          ControlPanel.this.increment = 5;
        }
      }
    });

    panel.add(this.rdbtnNewRadioButton_4);

    this.rdbtnNewRadioButton_5 = new JRadioButton("6");
    this.rdbtnNewRadioButton_5.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.rdbtnNewRadioButton_5.isSelected()) {
          ControlPanel.this.increment = 6;
        }
      }
    });

    panel.add(this.rdbtnNewRadioButton_5);
    this.group.add(this.rdbtnNewRadioButton);
    this.group.add(this.rdbtnNewRadioButton_1);
    this.group.add(this.rdbtnNewRadioButton_2);
    this.group.add(this.rdbtnNewRadioButton_3);
    this.group.add(this.rdbtnNewRadioButton_4);
    this.group.add(this.rdbtnNewRadioButton_5);

  }

  /**
   * @param minTime the minTime to set
   */
  public void setMinTime(int minTime) {
    this.scrollBar.setMinimum(minTime);
  }

  /**
   * @param maxTime the maxTime to set
   */
  public void setMaxTime(int maxTime) {
    this.scrollBar.setMaximum(maxTime);
  }

  /**
   * @param currentTime the currentTime to set
   */
  public void setCurrentTime(int currentTime) {
    this.scrollBar.setValue(currentTime);
    this.clock.setTime(currentTime);
  }

  public int getCurrentTime() {
    return this.scrollBar.getValue();
  }

  /**
   * @return the increment
   */
  public int getIncrement() {
    return this.increment;
  }

  void addAdjustmentListener(AdjustmentListener listener) {
    this.scrollBar.addAdjustmentListener(listener);
  }
}
