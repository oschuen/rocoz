package rocsim.schedule.model;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class LineSegmentModel {
  private String stationOne;
  private String stationTwo;
  private String watchBlock;

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("station-one", this.stationOne);
    builder.add("station-two", this.stationTwo);
    builder.add("watch-block", this.watchBlock);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.stationOne = obj.getString("station-one", "");
    this.stationTwo = obj.getString("station-two", "");
    this.watchBlock = obj.getString("watch-block", "");
  }

  /**
   * @return the stationOne
   */
  public String getStationOne() {
    return this.stationOne;
  }

  /**
   * @param stationOne the stationOne to set
   */
  public void setStationOne(String stationOne) {
    this.stationOne = stationOne;
  }

  /**
   * @return the stationTwo
   */
  public String getStationTwo() {
    return this.stationTwo;
  }

  /**
   * @param stationTwo the stationTwo to set
   */
  public void setStationTwo(String stationTwo) {
    this.stationTwo = stationTwo;
  }

  /**
   * @return the watchBlocK
   */
  public String getWatchBlock() {
    return this.watchBlock;
  }

  /**
   * @param watchBlocK the watchBlocK to set
   */
  public void setWatchBlock(String watchBlocK) {
    this.watchBlock = watchBlocK;
  }
}
