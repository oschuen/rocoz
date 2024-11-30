package rocsim.schedule;

import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rocsim.gui.Tile;
import rocsim.gui.Tile.UseState;
import rocsim.track.Block;
import rocsim.track.TrackPlan;

public class Scheduler {
  private TrackPlan plan;
  private List<Loco> locos;
  private List<Trip> trips;
  private int minTime = 0;
  private int maxTime = 0;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private interface Job {
    int getSchedule();

    void run(int time);
  }

  private class RunScheduleJob implements Job {

    Block block;
    Loco loco;
    Tile currentLocation;
    int scheduleTime;

    public RunScheduleJob(Block block, Loco loco, Tile currentLocation, int scheduleTime) {
      super();
      this.block = block;
      this.loco = loco;
      this.currentLocation = currentLocation;
      this.scheduleTime = scheduleTime;
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
        if (this.currentLocation instanceof rocsim.gui.Block) {
          if (((rocsim.gui.Block) this.currentLocation).isStellBlock()) {
            this.currentLocation.setState(UseState.BLOCK);
            this.loco.setInBw(true);
          }
          this.block.markUnBlocked();
          Scheduler.this.logger.info("[{}] Loco {} arrived at {}", time, this.loco.getId(),
              this.currentLocation.getId());
        }
      });
    }

  }

  private class StartScheduleJob implements Job {

    Loco loco;
    Schedule schedule;

    public StartScheduleJob(Schedule schedule, Loco loco) {
      super();
      this.schedule = schedule;
      this.loco = loco;
    }

    @Override
    public int getSchedule() {
      return this.schedule.getStartTime();
    }

    @Override
    public void run(int time) {
      Tile startTile = Scheduler.this.plan.getTile(this.schedule.getStartBlock());
      Tile endTile = Scheduler.this.plan.getTile(this.schedule.getEndBlock());
      boolean error = false;
      String message = "";
      Block block = null;
      if (startTile == null || endTile == null) {
        message = "Can't Block, " + this.schedule.getStartBlock() + " or " + this.schedule.getEndBlock()
            + " doesn't exist";
        error = true;
      } else {
        block = Scheduler.this.plan.getBlock(startTile, endTile);
        if (block.isEmpty()) {
          message = "Can't block from " + startTile.getId() + " " + " to " + endTile.getId();
          error = true;
        } else {
          if (this.loco.isInBw()) {
            this.loco.setLocation(startTile.getLocation());
            this.loco.setInBw(false);
          } else if (!this.loco.getLocation().equals(startTile.getLocation())) {
            message = "Loco " + this.loco.getId() + " is not at location " + startTile.getId();
            error = true;
          }
        }
      }
      if (error) {
        Scheduler.this.logger.error("Can't schedule {}", this.schedule);
        Scheduler.this.logger.error("Message {}", message);
      } else {
        block.markBlocked();
        block.layBlock();
        startTile.setState(UseState.TRAIN);
        int drivingTime = startTile.getDrivingTime(this.loco.getvMax());
        Scheduler.this.logger.info("[{}] Start Trip. Loco: {} from: {} to {}", time, this.loco.getId(),
            startTile.getId(), endTile.getId());
        Scheduler.this.jobList.add(new RunScheduleJob(block, this.loco, startTile, time + drivingTime));
      }
    }

  }

  private class StartTripJob implements Job {

    private Trip trip;

    public StartTripJob(Trip trip) {
      super();
      this.trip = trip;
    }

    @Override
    public int getSchedule() {
      return this.trip.getStartTime();
    }

    @Override
    public void run(int time) {
      Optional<Loco> loco = getLoco(this.trip.getTrainId());
      loco.ifPresentOrElse((theLoco) -> {
        for (Schedule schedule : this.trip.getSchedules()) {
          Scheduler.this.jobList.add(new StartScheduleJob(schedule, theLoco));
        }
      }, () -> {
        Scheduler.this.logger.error("Can't perform Trip {}. Lok Id {} not available", this.trip.getId(),
            this.trip.getTrainId());
      });
    }
  }

  private Queue<Job> jobList = new PriorityQueue<>(10, (a, b) -> {
    return Integer.compare(a.getSchedule(), b.getSchedule());
  });

  public Scheduler(TrackPlan plan, List<Trip> trips, List<Loco> locos) {
    super();
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
    }
    for (Trip trip : this.trips) {
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
