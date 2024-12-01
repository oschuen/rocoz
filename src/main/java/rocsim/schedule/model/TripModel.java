package rocsim.schedule.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class TripModel {
  private String id = "";
  private String locoId = "";
  private int startTime = 0;
  private String comment = "";

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("id", this.id);
    builder.add("loco-id", this.locoId);
    builder.add("start-time", this.startTime);
    builder.add("comment", this.comment);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.id = obj.getString("id", "");
    this.locoId = obj.getString("loco-id", "");
    this.startTime = obj.getInt("start-time", 0);
    this.comment = obj.getString("comment", "");
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

  /**
   * @param startTime the startTime to set
   */
  public void setStartTime(int startTime) {
    this.startTime = startTime;
  }
}
