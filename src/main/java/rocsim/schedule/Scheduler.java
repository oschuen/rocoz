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
package rocsim.schedule;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocsim.gui.tiles.Tile;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TrackPlanModel.BlockKind;
import rocsim.schedule.model.TripModel;
import rocsim.track.Block;
import rocsim.track.TrackPlan;

public class Scheduler {
  private TrackPlan plan;
  private TimeModel timeModel;
  private List<Loco> locos;
  private List<TripModel> trips;
  private int minTime = 0;
  private int maxTime = 0;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private interface Job {
    int getSchedule();

    void run(int time);
  }

  private class UnblockBlockJob implements Job {
    private Block block;
    private int scheduleTime;

    public UnblockBlockJob(Block block, int scheduleTime) {
      this.block = block;
      this.scheduleTime = scheduleTime;
    }

    @Override
    public int getSchedule() {
      return this.scheduleTime;
    }

    @Override
    public void run(int time) {
      this.block.markUnBlocked();
    }
  }

  private class RunScheduleJob implements Job {

    Block block;
    Loco loco;
    Tile currentLocation;
    int scheduleTime;
    int dontMoveTime;

    public RunScheduleJob(Block block, Loco loco, Tile currentLocation, int scheduleTime, int dontMoveTime) {
      super();
      this.block = block;
      this.loco = loco;
      this.currentLocation = currentLocation;
      this.scheduleTime = scheduleTime;
      this.dontMoveTime = dontMoveTime;
    }

    @Override
    public int getSchedule() {

      return this.scheduleTime;
    }

    @Override
    public void run(int time) {
      Optional<Tile> nextTile = this.block.getNextTile(this.currentLocation);
      nextTile.ifPresentOrElse((tile) -> {
        this.block.markBlocked();
        this.loco.setLocation(tile.getLocation());
        this.currentLocation = tile;
        this.scheduleTime = time + tile.getDrivingTime(this.loco.getvMax());
        tile.setState(UseState.TRAIN);
        Scheduler.this.jobList.add(this);
      }, () -> {
        if (this.currentLocation.getBlockKind() != BlockKind.NONE) {
          if (this.currentLocation.getBlockKind() == BlockKind.STELLBLOCK) {
            this.currentLocation.setState(UseState.BLOCK);
            this.loco.setInBw(true);
          }
          Scheduler.this.logger.info("[{}] Loco {} arrived at {}", time, this.loco.getId(),
              this.currentLocation.getId());
          Scheduler.this.jobList.add(new UnblockBlockJob(this.block, time + 60));
        }
        this.loco.setDontMoveTime(this.dontMoveTime);
      });
    }

  }

  private class StartScheduleJob implements Job {

    private Loco loco;
    private ScheduleModel schedule;
    private int startTime;

    public StartScheduleJob(ScheduleModel schedule, Loco loco, int startTime) {
      super();
      this.schedule = schedule;
      this.loco = loco;
      this.startTime = startTime;
    }

    @Override
    public int getSchedule() {
      return this.startTime;
    }

    @Override
    public void run(int time) {
      Tile startTile = Scheduler.this.plan.getTile(this.schedule.getStartBlock());
      Tile endTile = Scheduler.this.plan.getTile(this.schedule.getEndBlock());
      boolean error = false;
      Block block = null;
      if (startTile == null || endTile == null) {
        Scheduler.this.logger.error("[{}] Can't Block, {} or {} doesn't exist", time, this.schedule.getStartBlock(),
            this.schedule.getEndBlock());
        error = true;
      } else {
        block = Scheduler.this.plan.getBlock(startTile, endTile);
        if (block.isEmpty()) {
          Scheduler.this.logger.error("[{}] Can't block from {} to {}", time, startTile.getId(), endTile.getId());
          error = true;
        } else {
          if (this.loco.isInBw()) {
            this.loco.setLocation(startTile.getLocation());
            this.loco.setInBw(false);
          } else if (!this.loco.getLocation().equals(startTile.getLocation())) {

            Scheduler.this.logger.error("[{}] Loco {} is not at location  {}", time, this.loco.getId(),
                startTile.getId());
            error = true;
          }
        }
      }
      if (!error) {
        block.markBlocked();
        block.layBlock();
        startTile.setState(UseState.TRAIN);
        int drivingTime = startTile.getDrivingTime(this.loco.getvMax());
        Scheduler.this.logger.info("[{}] Start Trip. Loco: {} from: {} to {}", time, this.loco.getId(),
            startTile.getId(), endTile.getId());
        Scheduler.this.jobList.add(new RunScheduleJob(block, this.loco, startTile, time + drivingTime,
            time + this.schedule.getDuration() + this.schedule.getPause()));
      }
    }

  }

  private class StartTripJob implements Job {

    private TripModel trip;

    public StartTripJob(TripModel trip) {
      super();
      this.trip = trip;
    }

    @Override
    public int getSchedule() {
      return this.trip.getStartTime();
    }

    @Override
    public void run(int time) {
      Optional<Loco> loco = getLoco(this.trip.getLocoId());
      loco.ifPresentOrElse((theLoco) -> {
        int tempTime = time;
        if (theLoco.getDontMoveTime() > time) {
          Scheduler.this.logger.error("[{}] Can't perform Trip {}. Lok Id {} still in pause till {}", time,
              this.trip.getId(), this.trip.getLocoId(),
              Scheduler.this.timeModel.getTimeSecString(theLoco.getDontMoveTime()));
        } else {
          theLoco.setCurrentTrain(this.trip.getId());
          for (ScheduleModel schedule : this.trip.getSchedules()) {
            Scheduler.this.jobList.add(new StartScheduleJob(schedule, theLoco, tempTime));
            tempTime += schedule.getDuration() + schedule.getPause();
          }
        }
      }, () -> {
        Scheduler.this.logger.error("[{}] Can't perform Trip {}. Lok Id {} not available", time, this.trip.getId(),
            this.trip.getLocoId());
      });
    }
  }

  private Queue<Job> jobList = new PriorityQueue<>(10, (a, b) -> {
    return Integer.compare(a.getSchedule(), b.getSchedule());
  });

  public Scheduler(TrackPlan plan, List<TripModel> trips, List<Loco> locos, TimeModel timeModel) {
    super();
    this.timeModel = timeModel;
    this.plan = plan;
    this.locos = locos;
    this.trips = trips;
    setUpScheduler();
  }

  private Optional<Loco> getLoco(String id) {
    for (Loco loco : this.locos) {
      if (loco.getId().equals(id)) {
        return Optional.of(loco);
      }
    }
    return Optional.empty();
  }

  /**
   * @return the minTime
   */
  public int getMinTime() {
    return this.minTime;
  }

  /**
   * @return the maxTime
   */
  public int getMaxTime() {
    return this.maxTime;
  }

  private void setUpScheduler() {
    boolean first = true;
    this.jobList.clear();
    this.plan.reset();
    for (Loco loco : this.locos) {
      loco.setInBw(true);
      loco.setDontMoveTime(Integer.MIN_VALUE);
    }
    for (TripModel trip : this.trips) {
      this.jobList.add(new StartTripJob(trip));
      if (first) {
        this.minTime = trip.getStartTime();
        this.maxTime = trip.getEndTime();
        first = false;
      } else {
        this.maxTime = trip.getEndTime();
      }
    }
  }

  public void resetTo(int time) {
    setUpScheduler();
    fastForwardTo(time);
  }

  public void fastForwardTo(int time) {
    Job nextJob = this.jobList.peek();
    boolean end = false;
    if (nextJob != null) {
      while (!(end || nextJob == null)) {
        int jobTime = nextJob.getSchedule();
        if (jobTime > time) {
          end = true;
        } else {
          this.timeModel.setCurrentTime(jobTime);
          nextJob = this.jobList.poll();
          nextJob.run(jobTime);
          nextJob = this.jobList.peek();
        }
      }
    }
  }

  public boolean schedule(int time) {
    Job nextJob = this.jobList.peek();
    boolean worked = false;
    while (nextJob != null && nextJob.getSchedule() <= time) {
      nextJob = this.jobList.poll();
      nextJob.run(time);
      nextJob = this.jobList.peek();
      worked = true;
    }
    return worked;
  }
}
