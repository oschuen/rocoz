package rocsim.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollBar;

import rocsim.schedule.Scheduler;
import rocsim.track.TrackPlan;
import rocsim.xml.ReadPlan;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private Scheduler scheduler;
  private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
  private int currentTime = 55;
  private int currentWishTime = 55;
  private PlanPannel panel;
  private JScrollBar scrollBar;

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));
    TrackPlan plan = new TrackPlan(planner.getTiles());
    this.panel = new PlanPannel(plan);
    this.scheduler = new Scheduler(plan, planner.getTrips(), planner.getLocos());

    getContentPane().add(this.panel, BorderLayout.CENTER);

    this.scrollBar = new JScrollBar();
    this.scrollBar.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent arg0) {
        MainFrame.this.currentWishTime = MainFrame.this.scrollBar.getValue();
      }
    });
    this.currentTime = this.scheduler.getMinTime() - 2;
    this.currentWishTime = this.currentTime;
    this.scrollBar.setMinimum(this.scheduler.getMinTime());
    this.scrollBar.setMaximum(this.scheduler.getMaxTime());
    this.scrollBar.setValue(this.scheduler.getMinTime());
    this.scrollBar.setOrientation(JScrollBar.HORIZONTAL);
    getContentPane().add(this.scrollBar, BorderLayout.SOUTH);
    pack();

    this.service.scheduleAtFixedRate(() -> {
      try {
        boolean repaint = false;
        if (Math.abs(this.currentWishTime - this.currentTime) > 5) {
          if (this.currentWishTime > this.currentTime) {
            this.scheduler.fastForwardTo(this.currentWishTime);
          } else {
            this.scheduler.resetTo(this.currentWishTime);
          }
          this.currentTime = this.currentWishTime;
          repaint = true;
        } else {
          this.currentTime++;
          if (this.scheduler.schedule(this.currentTime)) {
            repaint = true;
          }
        }
        this.scrollBar.setValue(this.currentTime);
        if (repaint) {
          java.awt.EventQueue.invokeLater(() -> {
            this.panel.repaint();
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
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
