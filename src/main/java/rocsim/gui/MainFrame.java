package rocsim.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;

import rocsim.schedule.Scheduler;
import rocsim.track.TrackPlan;
import rocsim.xml.ReadPlan;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private Scheduler scheduler;
  private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
  private int currentTime = 55;
  private PlanPannel panel;

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));
    TrackPlan plan = new TrackPlan(planner.getTiles());
    this.panel = new PlanPannel(plan);
    getContentPane().add(this.panel, BorderLayout.CENTER);
    pack();

    this.scheduler = new Scheduler(plan, planner.getTrips(), planner.getLocos());
    this.service.scheduleAtFixedRate(() -> {
      this.currentTime++;
      if (this.scheduler.schedule(this.currentTime)) {
        java.awt.EventQueue.invokeLater(() -> {
          this.panel.repaint();
        });
      }
    }, 1, 1, TimeUnit.SECONDS);
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
