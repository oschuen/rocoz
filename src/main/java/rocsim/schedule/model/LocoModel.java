package rocsim.schedule.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class LocoModel {
  private String id = "";
  private int vMax = 80;
  private String comment = "";

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("id", this.id);
    builder.add("v-max", this.vMax);
    builder.add("comment", this.comment);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.id = obj.getString("id", "");
    this.vMax = obj.getInt("v-max", 80);
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
   * @return the vMax
   */
  public int getvMax() {
    return this.vMax;
  }

  /**
   * @param vMax the vMax to set
   */
  public void setvMax(int vMax) {
    this.vMax = vMax;
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
}
