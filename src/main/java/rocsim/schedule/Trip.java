package rocsim.schedule;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trip {
  private String id = "";
  private String trainId = "";
  private List<Schedule> schedules = new ArrayList<>();
  private int startTime = 0;
  private int endTime = 0;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  public Trip(String trainId, String id) {
    super();
    this.trainId = trainId;
    this.id = id;
  }

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @return the trainId
   */
  public String getTrainId() {
    return this.trainId;
  }

  /**
   * @return the startTime
   */
  public int getStartTime() {
    return this.startTime;
  }

  /**
   * @return the endTime
   */
  public int getEndTime() {
    return this.endTime;
  }

  /**
   * @return the schedules
   */
  public List<Schedule> getSchedules() {
    return this.schedules;
  }

  public void addSchedule(Schedule schedule) {
    if (this.schedules.isEmpty()) {
      this.startTime = schedule.getStartTime();
      this.endTime = schedule.getEndTime() + schedule.getMinWaitTime();
    } else {
      if (schedule.getStartTime() < this.endTime) {
        this.logger.error("This Schedule {} starts to early", this.id);
        this.logger.error(schedule.toString());
        this.logger.error("Earlies allowed time is {}", this.endTime);
      }
      this.endTime = Math.max(schedule.getEndTime() + schedule.getMinWaitTime(), this.endTime);
    }
    this.schedules.add(schedule);
  }

}
