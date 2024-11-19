package rocsim.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;

import rocsim.xml.ReadPlan;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));
    PlanPannel panel = new PlanPannel(planner.getTiles());
    getContentPane().add(panel, BorderLayout.CENTER);
    pack();
  }

  @Override
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  public void jMenuFileExit_actionPerformed(ActionEvent e) {

    System.exit(0);
  }

}
