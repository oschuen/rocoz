package rocsim.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;

public class ControlPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  private JScrollBar scrollBar;
  private ClockPanel clock;
  private ClockPanel fremoClock;
  private JRadioButton animationSpeedButton_1;
  private JRadioButton animationSpeedButton_2;
  private JRadioButton animationSpeedButton_3;
  private JRadioButton animationSpeedButton_4;
  private JRadioButton animationSpeedButton_5;
  private JRadioButton animationSpeedButton_6;
  private ButtonGroup animationSpeedButtonGroup = new ButtonGroup();

  private JRadioButton fremoTimeButton_1;
  private JRadioButton fremoTimeButton_2;
  private JRadioButton fremoTimeButton_3;
  private JRadioButton fremoTimeButton_4;
  private JRadioButton fremoTimeButton_5;
  private JRadioButton fremoTimeButton_6;
  private ButtonGroup fremoTimeButtonGroup = new ButtonGroup();
  private int increment = 1;
  private int fremoTimeIncrement = 1;

  public ControlPanel() {
    setLayout(new BorderLayout(10, 10));

    this.fremoClock = new ClockPanel(0, false);
    add(this.fremoClock, BorderLayout.EAST);

    this.clock = new ClockPanel(0, true);
    add(this.clock, BorderLayout.WEST);

    this.scrollBar = new JScrollBar();
    add(this.scrollBar, BorderLayout.SOUTH);
    this.scrollBar.setOrientation(JScrollBar.HORIZONTAL);

    JPanel panel2 = new JPanel();
    panel2.setLayout(new GridLayout(1, 2, 0, 0));
    add(panel2, BorderLayout.CENTER);

    JPanel animationTimePanel = new JPanel();
    animationTimePanel.setLayout(new GridLayout(3, 2, 0, 0));
    animationTimePanel.setBorder(BorderFactory.createTitledBorder("Animation-Speed"));
    panel2.add(animationTimePanel);

    JPanel fremoTimePanel = new JPanel();
    fremoTimePanel.setLayout(new GridLayout(3, 2, 0, 0));
    fremoTimePanel.setBorder(BorderFactory.createTitledBorder("Time Speed"));
    panel2.add(fremoTimePanel);

    ActionListener animationSpeedActionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.animationSpeedButton_1.isSelected()) {
          ControlPanel.this.increment = 1;
        } else if (ControlPanel.this.animationSpeedButton_2.isSelected()) {
          ControlPanel.this.increment = 2;
        } else if (ControlPanel.this.animationSpeedButton_3.isSelected()) {
          ControlPanel.this.increment = 3;
        } else if (ControlPanel.this.animationSpeedButton_4.isSelected()) {
          ControlPanel.this.increment = 4;
        } else if (ControlPanel.this.animationSpeedButton_5.isSelected()) {
          ControlPanel.this.increment = 5;
        } else if (ControlPanel.this.animationSpeedButton_6.isSelected()) {
          ControlPanel.this.increment = 6;
        }
      }
    };

    ActionListener fremoTimeActionListener = new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        if (ControlPanel.this.fremoTimeButton_1.isSelected()) {
          ControlPanel.this.fremoTimeIncrement = 1;
        } else if (ControlPanel.this.fremoTimeButton_2.isSelected()) {
          ControlPanel.this.fremoTimeIncrement = 2;
        } else if (ControlPanel.this.fremoTimeButton_3.isSelected()) {
          ControlPanel.this.fremoTimeIncrement = 3;
        } else if (ControlPanel.this.fremoTimeButton_4.isSelected()) {
          ControlPanel.this.fremoTimeIncrement = 4;
        } else if (ControlPanel.this.fremoTimeButton_5.isSelected()) {
          ControlPanel.this.fremoTimeIncrement = 5;
        } else if (ControlPanel.this.fremoTimeButton_6.isSelected()) {
          ControlPanel.this.fremoTimeIncrement = 6;
        }
      }
    };

    this.animationSpeedButton_1 = new JRadioButton("1");
    this.animationSpeedButton_1.setSelected(true);
    this.animationSpeedButton_1.addActionListener(animationSpeedActionListener);

    animationTimePanel.add(this.animationSpeedButton_1);

    this.animationSpeedButton_2 = new JRadioButton("2");
    this.animationSpeedButton_2.addActionListener(animationSpeedActionListener);

    animationTimePanel.add(this.animationSpeedButton_2);

    this.animationSpeedButton_3 = new JRadioButton("3");
    this.animationSpeedButton_3.addActionListener(animationSpeedActionListener);

    animationTimePanel.add(this.animationSpeedButton_3);

    this.animationSpeedButton_4 = new JRadioButton("4");
    this.animationSpeedButton_4.addActionListener(animationSpeedActionListener);

    animationTimePanel.add(this.animationSpeedButton_4);

    this.animationSpeedButton_5 = new JRadioButton("5");
    this.animationSpeedButton_5.addActionListener(animationSpeedActionListener);

    animationTimePanel.add(this.animationSpeedButton_5);

    this.animationSpeedButton_6 = new JRadioButton("6");
    this.animationSpeedButton_6.addActionListener(animationSpeedActionListener);

    animationTimePanel.add(this.animationSpeedButton_6);
    this.animationSpeedButtonGroup.add(this.animationSpeedButton_1);
    this.animationSpeedButtonGroup.add(this.animationSpeedButton_2);
    this.animationSpeedButtonGroup.add(this.animationSpeedButton_3);
    this.animationSpeedButtonGroup.add(this.animationSpeedButton_4);
    this.animationSpeedButtonGroup.add(this.animationSpeedButton_5);
    this.animationSpeedButtonGroup.add(this.animationSpeedButton_6);

    this.fremoTimeButton_1 = new JRadioButton("1");
    this.fremoTimeButton_1.setSelected(true);
    this.fremoTimeButton_1.addActionListener(fremoTimeActionListener);

    fremoTimePanel.add(this.fremoTimeButton_1);

    this.fremoTimeButton_2 = new JRadioButton("2");
    this.fremoTimeButton_2.addActionListener(fremoTimeActionListener);

    fremoTimePanel.add(this.fremoTimeButton_2);

    this.fremoTimeButton_3 = new JRadioButton("3");
    this.fremoTimeButton_3.addActionListener(fremoTimeActionListener);

    fremoTimePanel.add(this.fremoTimeButton_3);

    this.fremoTimeButton_4 = new JRadioButton("4");
    this.fremoTimeButton_4.addActionListener(fremoTimeActionListener);

    fremoTimePanel.add(this.fremoTimeButton_4);

    this.fremoTimeButton_5 = new JRadioButton("5");
    this.fremoTimeButton_5.addActionListener(fremoTimeActionListener);

    fremoTimePanel.add(this.fremoTimeButton_5);

    this.fremoTimeButton_6 = new JRadioButton("6");
    this.fremoTimeButton_6.addActionListener(fremoTimeActionListener);

    fremoTimePanel.add(this.fremoTimeButton_6);

    this.fremoTimeButtonGroup.add(this.fremoTimeButton_1);
    this.fremoTimeButtonGroup.add(this.fremoTimeButton_2);
    this.fremoTimeButtonGroup.add(this.fremoTimeButton_3);
    this.fremoTimeButtonGroup.add(this.fremoTimeButton_4);
    this.fremoTimeButtonGroup.add(this.fremoTimeButton_5);
    this.fremoTimeButtonGroup.add(this.fremoTimeButton_6);

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
    this.fremoClock.setTime(currentTime * this.fremoTimeIncrement);
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
