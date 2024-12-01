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
import javax.swing.JTabbedPane;

import rocsim.gui.editor.EditorContainer;
import rocsim.schedule.Scheduler;
import rocsim.schedule.model.TimeModel;
import rocsim.track.TrackPlan;
import rocsim.xml.ReadPlan;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private Scheduler scheduler;
  private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
  private int currentTime = 55;
  private int currentWishTime = 55;
  private PlanPanel panel;
  private LogPanel logPanel;
  private ControlPanel controlPanel;
  private JTabbedPane tabbedPane;
  private EditorContainer container;
  private TimeModel timeModel = new TimeModel();

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));

    this.tabbedPane = new JTabbedPane();

    TrackPlan plan = new TrackPlan(planner.getTiles());
    this.panel = new PlanPanel(plan, planner.getLocos());
    this.scheduler = new Scheduler(plan, planner.getTrips(), planner.getLocos());
    this.timeModel.setBase(this.scheduler.getMinTime());

    this.logPanel = new LogPanel(this.timeModel);
    this.container = new EditorContainer(planner, this.timeModel);

    this.tabbedPane.addTab("Track", this.panel);
    this.tabbedPane.addTab("Locos", this.container.getLocoFrame());
    this.tabbedPane.addTab("Trips", this.container.getTripFrame());
    this.tabbedPane.addTab("Logs", this.logPanel);

    getContentPane().add(this.tabbedPane, BorderLayout.CENTER);

    this.controlPanel = new ControlPanel(this.timeModel);

    this.controlPanel.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent arg0) {
        MainFrame.this.currentWishTime = MainFrame.this.controlPanel.getCurrentTime();
      }
    });
    this.currentTime = this.timeModel.getCurrentTime();
    this.currentWishTime = this.currentTime;
    this.controlPanel.setMinTime(this.scheduler.getMinTime());
    this.controlPanel.setMaxTime(this.scheduler.getMaxTime());
    this.controlPanel.applyCurrentTime();
    getContentPane().add(this.controlPanel, BorderLayout.SOUTH);
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
        } else if (!this.controlPanel.isPaused()) {
          for (int i = 0; i < this.controlPanel.getIncrement(); i++) {
            this.currentTime++;
            this.timeModel.setCurrentTime(this.currentTime);
            if (this.scheduler.schedule(this.currentTime)) {
              repaint = true;
            }
          }
        }
        this.controlPanel.applyCurrentTime();
        this.currentWishTime = this.currentTime;
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
