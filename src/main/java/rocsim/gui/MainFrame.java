package rocsim.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import rocsim.gui.animation.AnimationContainer;
import rocsim.gui.editor.EditorContainer;
import rocsim.schedule.model.TimeModel;
import rocsim.xml.ReadPlan;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private JTabbedPane tabbedPane;
  private EditorContainer editorContainer;
  private AnimationContainer animationContainer;
  private TimeModel timeModel = new TimeModel();

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));

    this.tabbedPane = new JTabbedPane();

    this.editorContainer = new EditorContainer(planner, this.timeModel);
    this.animationContainer = new AnimationContainer(planner, this.timeModel);

    this.tabbedPane.addTab("Track", this.animationContainer.getPlanPannel());
    this.tabbedPane.addTab("Locos", this.editorContainer.getLocoFrame());
    this.tabbedPane.addTab("Trips", this.editorContainer.getTripFrame());
    this.tabbedPane.addTab("Logs", this.animationContainer.getLogPanel());

    getContentPane().add(this.tabbedPane, BorderLayout.CENTER);

    getContentPane().add(this.animationContainer.getControlPanel(), BorderLayout.SOUTH);
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
