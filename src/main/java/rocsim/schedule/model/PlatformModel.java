package rocsim.schedule.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class PlatformModel {
  private String name = "";
  private String blockId;

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("name", this.name);
    builder.add("block-id", this.blockId);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.name = obj.getString("name", "");
    this.blockId = obj.getString("block-id", "");
  }

  /**
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the blockId
   */
  public String getBlockId() {
    return this.blockId;
  }

  /**
   * @param blockId the blockId to set
   */
  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

}
