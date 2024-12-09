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
package rocsim.gui.animation;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.json.JsonArray;
import javax.json.JsonObject;

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.schedule.Loco;
import rocsim.schedule.Scheduler;
import rocsim.schedule.model.LocoModel;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TrackPlanModel;
import rocsim.schedule.model.TripModel;
import rocsim.track.TrackPlan;

public class AnimationContainer {
  private int currentWishTime = 55;

  // private Scheduler scheduler;
  private PlanPanel planPannel;
  private LogPanel logPanel;
  private ControlPanel controlPanel;
  private BlockUsePanel blockUsePanel;
  private BlockStatusModel blockStatusModel;
  private TrackPlan plan;
  private ScheduledFuture<?> executionFuture;
  private ExecutorJob executorJob;
  private Lock lock = new ReentrantLock();

  private TimeModel timeModel;
  private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

  private StringListDataModel blockIdDataModel = new StringListDataModel();

  public AnimationContainer(TimeModel timeModel) {

    this.timeModel = timeModel;
    this.blockStatusModel = new BlockStatusModel(this.blockIdDataModel, timeModel);

    this.plan = new TrackPlan();

    this.planPannel = new PlanPanel(this.plan);

    this.logPanel = new LogPanel(this.timeModel);
    this.controlPanel = new ControlPanel(this.timeModel);

    this.controlPanel.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent arg0) {
        AnimationContainer.this.currentWishTime = AnimationContainer.this.controlPanel.getCurrentTime();
      }
    });
    this.controlPanel.setMinTime(0);
    this.controlPanel.setMaxTime(3600);
    this.controlPanel.applyCurrentTime();

    this.blockUsePanel = new BlockUsePanel(this.blockStatusModel, this.blockIdDataModel, timeModel);
//    this.executorJob = new ExecutorJob(this.scheduler, this.blockUsePanel, this.planPannel, this.controlPanel,
//        timeModel);
//    this.executionFuture = this.service.scheduleAtFixedRate(this.executorJob, 1, 1, TimeUnit.SECONDS);
  }

  public void fromJson(JsonObject obj) {

    JsonArray locoArr = obj.getJsonArray("locos");
    List<Loco> locos = new ArrayList<>();
    for (int i = 0; i < locoArr.size(); i++) {
      LocoModel model = new LocoModel();
      model.fromJson(locoArr.getJsonObject(i));
      Loco loco = new Loco(model);
      locos.add(loco);
    }
    TrackPlanModel trackModel = new TrackPlanModel();
    trackModel.fromJson(obj.getJsonArray("track"));
    this.blockIdDataModel.setValueList(trackModel.getWatchBlockIds());
    this.plan.setTilesList(trackModel.generateTiles(this.blockStatusModel));
    this.planPannel.setLocos(locos);
    JsonArray tripsArr = obj.getJsonArray("trips");
    List<TripModel> trips = new ArrayList<>();
    for (int i = 0; i < tripsArr.size(); i++) {
      TripModel model = new TripModel();
      model.fromJson(tripsArr.getJsonObject(i));
      trips.add(model);
    }
    this.lock.lock();
    try {
      Scheduler scheduler = new Scheduler(this.plan, trips, locos, this.timeModel);
      this.timeModel.setMinTime(scheduler.getMinTime());
      this.timeModel.setMaxTime(scheduler.getMaxTime());
      this.controlPanel.setMinTime(scheduler.getMinTime());
      this.controlPanel.setMaxTime(scheduler.getMaxTime());
      this.currentWishTime = scheduler.getMinTime();
      this.timeModel.setCurrentTime(scheduler.getMinTime());
      if (this.executionFuture != null) {
        this.executionFuture.cancel(false);
        this.executorJob.stop();
      }
      this.executorJob = new ExecutorJob(scheduler, this.blockUsePanel, this.planPannel, this.controlPanel,
          this.timeModel);
      this.executionFuture = this.service.scheduleAtFixedRate(this.executorJob, 1, 1, TimeUnit.SECONDS);
    } finally {
      this.lock.unlock();
    }
  }

  private class ExecutorJob implements Runnable {

    private Scheduler scheduler;
    private TimeModel timeModel;
    private ControlPanel controlPanel;
    private BlockUsePanel blockUsePanel;
    private PlanPanel planPannel;
    private boolean running = true;

    public ExecutorJob(Scheduler scheduler, BlockUsePanel blockUsePanel, PlanPanel planPannel,
        ControlPanel controlPanel, TimeModel timeModel) {
      super();
      this.scheduler = scheduler;
      this.blockUsePanel = blockUsePanel;
      this.planPannel = planPannel;
      this.controlPanel = controlPanel;
      this.timeModel = timeModel;
    }

    public void stop() {
      AnimationContainer.this.lock.lock();
      try {
        this.running = false;
      } finally {
        AnimationContainer.this.lock.unlock();
      }
    }

    @Override
    public void run() {
      AnimationContainer.this.lock.lock();
      try {
        if (this.running) {
          innerRun();
        }
      } finally {
        AnimationContainer.this.lock.unlock();
      }
    }

    private void innerRun() {
      try {
        boolean repaint = false;
        if (this.scheduler != null) {
          if (Math.abs(AnimationContainer.this.currentWishTime - this.timeModel.getCurrentTime()) > 5) {
            if (AnimationContainer.this.currentWishTime > this.timeModel.getCurrentTime()) {
              this.scheduler.fastForwardTo(AnimationContainer.this.currentWishTime);
            } else {
              this.scheduler.resetTo(AnimationContainer.this.currentWishTime);
            }
            this.timeModel.setCurrentTime(AnimationContainer.this.currentWishTime);
            repaint = true;
          } else if (!this.controlPanel.isPaused()) {
            for (int i = 0; i < this.controlPanel.getIncrement(); i++) {
              AnimationContainer.this.timeModel.incrementCurrentTime();
              if (this.scheduler.schedule(this.timeModel.getCurrentTime())) {
                repaint = true;
              }
            }
          }
        }
        this.controlPanel.applyCurrentTime();
        AnimationContainer.this.currentWishTime = this.timeModel.getCurrentTime();
        if (AnimationContainer.this.currentWishTime % 4 == 0 || repaint) {
          javax.swing.SwingUtilities.invokeLater(() -> {
            this.blockUsePanel.triggerRebuild();
          });
        }
        if (repaint) {
          javax.swing.SwingUtilities.invokeLater(() -> {
            this.planPannel.repaint();
          });
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * @return the panel
   */
  public PlanPanel getPlanPannel() {
    return this.planPannel;
  }

  /**
   * @return the logPanel
   */
  public LogPanel getLogPanel() {
    return this.logPanel;
  }

  /**
   * @return the controlPanel
   */
  public ControlPanel getControlPanel() {
    return this.controlPanel;
  }

  /**
   * @return the blockUsePanel
   */
  public BlockUsePanel getBlockUsePanel() {
    return this.blockUsePanel;
  }
}