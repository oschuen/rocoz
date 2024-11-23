package rocsim.schedule;

import java.util.ArrayList;
import java.util.List;

public class Trip {
  private String id = "";
  private String trainId = "";
  private String followUpTripId = "";
  private List<Schedule> schedules = new ArrayList<>();
  private int startTime = 0;
  private int endTime = 0;

  public Trip(String trainId, String id, String followUpTripId) {
    super();
    this.trainId = trainId;
    this.id = id;
    this.followUpTripId = followUpTripId;
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
   * @return the followUpTripId
   */
  public String getFollowUpTripId() {
    return this.followUpTripId;
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
        System.err.println("This Schedule of " + this.id + " starts to early");
        System.err.println(schedule);
        System.err.println("Earlies allowed time is " + this.endTime);
      }
      this.endTime = Math.max(schedule.getEndTime() + schedule.getMinWaitTime(), this.endTime);
    }
    this.schedules.add(schedule);
  }

}
