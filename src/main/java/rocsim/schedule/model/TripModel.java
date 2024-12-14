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
  private String station = "";
  private String platform = "";
  private int startTime = 0;
  private String comment = "";
  private List<ScheduleStationModel> schedules = new ArrayList<>();

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("id", this.id);
    builder.add("loco-id", this.locoId);
    builder.add("station", this.station);
    builder.add("platform", this.platform);
    builder.add("start-time", this.startTime);
    builder.add("comment", this.comment);
    JsonArrayBuilder jSched = Json.createArrayBuilder();
    for (ScheduleStationModel scheduleModel : this.schedules) {
      jSched.add(scheduleModel.toJson());
    }
    builder.add("schedules", jSched);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.schedules.clear();
    this.id = obj.getString("id", "");
    this.locoId = obj.getString("loco-id", "");
    this.station = obj.getString("station", "");
    this.platform = obj.getString("platform", "");
    this.startTime = obj.getInt("start-time", 0);
    this.comment = obj.getString("comment", "");
    JsonArray jSched = obj.getJsonArray("schedules");
    if (jSched != null) {
      for (int i = 0; i < jSched.size(); i++) {
        ScheduleStationModel schedModel = new ScheduleStationModel();
        schedModel.fromJson(jSched.getJsonObject(i));
        addSchedule(schedModel);
      }
    }
  }

  public void addSchedule(ScheduleStationModel model) {
    this.schedules.add(model);
  }

  public List<ScheduleStationModel> getSchedules() {
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
    for (ScheduleStationModel scheduleModel : this.schedules) {
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

  /**
   * @return the station
   */
  public String getStation() {
    return this.station;
  }

  /**
   * @param station the station to set
   */
  public void setStation(String station) {
    this.station = station;
  }

  /**
   * @return the platform
   */
  public String getPlatform() {
    return this.platform;
  }

  /**
   * @param platform the platform to set
   */
  public void setPlatform(String platform) {
    this.platform = platform;
  }
}
