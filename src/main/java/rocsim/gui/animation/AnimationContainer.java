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
import java.util.concurrent.TimeUnit;

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

  private Scheduler scheduler;
  private PlanPanel planPannel;
  private LogPanel logPanel;
  private ControlPanel controlPanel;
  private BlockUsePanel blockUsePanel;
  private BlockStatusModel blockStatusModel;
  private TrackPlan plan;

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
    this.service.scheduleAtFixedRate(() -> {
      try {
        boolean repaint = false;
        if (this.scheduler != null) {
          if (Math.abs(this.currentWishTime - this.timeModel.getCurrentTime()) > 5) {
            if (this.currentWishTime > this.timeModel.getCurrentTime()) {
              this.scheduler.fastForwardTo(this.currentWishTime);
            } else {
              this.scheduler.resetTo(this.currentWishTime);
            }
            this.timeModel.setCurrentTime(this.currentWishTime);
            repaint = true;
          } else if (!this.controlPanel.isPaused()) {
            for (int i = 0; i < this.controlPanel.getIncrement(); i++) {
              timeModel.incrementCurrentTime();
              if (this.scheduler.schedule(this.timeModel.getCurrentTime())) {
                repaint = true;
              }
            }
          }
        }
        this.controlPanel.applyCurrentTime();
        this.currentWishTime = this.timeModel.getCurrentTime();
        if (this.currentWishTime % 4 == 0 || repaint) {
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
    }, 1, 1, TimeUnit.SECONDS);
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
    this.blockIdDataModel.setValueList(trackModel.getBlockIds());
    this.plan.setTilesList(trackModel.generateTiles(this.blockStatusModel));
    JsonArray tripsArr = obj.getJsonArray("trips");
    List<TripModel> trips = new ArrayList<>();
    for (int i = 0; i < tripsArr.size(); i++) {
      TripModel model = new TripModel();
      model.fromJson(tripsArr.getJsonObject(i));
      trips.add(model);
    }
    this.scheduler = new Scheduler(this.plan, trips, locos, this.timeModel);
    this.timeModel.setMinTime(this.scheduler.getMinTime());
    this.timeModel.setMaxTime(this.scheduler.getMaxTime());
    this.currentWishTime = this.scheduler.getMinTime();
    this.timeModel.setCurrentTime(this.scheduler.getMinTime());

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