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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.schedule.Scheduler;
import rocsim.schedule.model.TimeModel;
import rocsim.track.TrackPlan;
import rocsim.xml.ReadPlan;

public class AnimationContainer {
  private int currentWishTime = 55;

  private Scheduler scheduler;
  private PlanPanel planPannel;
  private LogPanel logPanel;
  private ControlPanel controlPanel;
  private BlockUsePanel blockUsePanel;
  private BlockStatusModel blockStatusModel;

  private TimeModel timeModel;
  private ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

  private StringListDataModel blockIdDataModel = new StringListDataModel();

  public AnimationContainer(ReadPlan planner, TimeModel timeModel) {

    this.timeModel = timeModel;
    this.blockIdDataModel.setValueList(planner.getTrackModel().getBlockIds());
    this.blockStatusModel = new BlockStatusModel(this.blockIdDataModel, timeModel);

    TrackPlan plan = new TrackPlan(planner.getTrackModel().generateTiles(this.blockStatusModel));
    this.planPannel = new PlanPanel(plan, planner.getLocos());
    this.scheduler = new Scheduler(plan, planner.getTripModels(), planner.getLocos(), timeModel);
    this.timeModel.setMinTime(this.scheduler.getMinTime());
    this.timeModel.setMaxTime(this.scheduler.getMaxTime());

    this.logPanel = new LogPanel(this.timeModel);
    this.controlPanel = new ControlPanel(this.timeModel);

    this.controlPanel.addAdjustmentListener(new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent arg0) {
        AnimationContainer.this.currentWishTime = AnimationContainer.this.controlPanel.getCurrentTime();
      }
    });
    this.controlPanel.setMinTime(this.scheduler.getMinTime());
    this.controlPanel.setMaxTime(this.scheduler.getMaxTime());
    this.controlPanel.applyCurrentTime();

    this.blockUsePanel = new BlockUsePanel(this.blockStatusModel, this.blockIdDataModel, timeModel);
    this.service.scheduleAtFixedRate(() -> {
      try {
        boolean repaint = false;
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