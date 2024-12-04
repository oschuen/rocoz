package rocsim.schedule.model;

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class TripModel {
  private String id = "";
  private String locoId = "";
  private int startTime = 0;
  private String comment = "";
  private List<ScheduleModel> schedules = new ArrayList<>();

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("id", this.id);
    builder.add("loco-id", this.locoId);
    builder.add("start-time", this.startTime);
    builder.add("comment", this.comment);
    JsonArrayBuilder jSched = Json.createArrayBuilder();
    for (ScheduleModel scheduleModel : this.schedules) {
      jSched.add(scheduleModel.toJson());
    }
    builder.add("schedules", jSched);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.schedules.clear();
    this.id = obj.getString("id", "");
    this.locoId = obj.getString("loco-id", "");
    this.startTime = obj.getInt("start-time", 0);
    this.comment = obj.getString("comment", "");
    JsonArray jSched = obj.getJsonArray("schedules");
    if (jSched != null) {
      for (int i = 0; i < jSched.size(); i++) {
        ScheduleModel schedModel = new ScheduleModel();
        schedModel.fromJson(jSched.getJsonObject(i));
        addSchedule(schedModel);
      }
    }
  }

  public void addSchedule(ScheduleModel model) {
    this.schedules.add(model);
  }

  public List<ScheduleModel> getSchedules() {
    return new ArrayList<>(this.schedules);
  }

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the comment
   */
  public String getComment() {
    return this.comment;
  }

  /**
   * @param comment the comment to set
   */
  public void setComment(String comment) {
    this.comment = comment;
  }

  /**
   * @return the locoId
   */
  public String getLocoId() {
    return this.locoId;
  }

  /**
   * @param locoId the locoId to set
   */
  public void setLocoId(String locoId) {
    this.locoId = locoId;
  }

  /**
   * @return the startTime
   */
  public int getStartTime() {
    return this.startTime;
  }

  public int getEndTime() {
    int time = this.startTime;
    for (ScheduleModel scheduleModel : this.schedules) {
      time += scheduleModel.getDuration() + scheduleModel.getPause();
    }
    return time;
  }

  /**
   * @param startTime the startTime to set
   */
  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }
}
