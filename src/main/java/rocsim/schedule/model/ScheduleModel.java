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

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

public class ScheduleModel {
  private String startBlock = "";
  private String endBlock = "";
  private int duration = 0;
  private int pause = 0;
  private String comment = "";

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("start-block", this.startBlock);
    builder.add("end-block", this.endBlock);
    builder.add("duration", this.duration);
    builder.add("pause", this.pause);
    builder.add("comment", this.comment);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.startBlock = obj.getString("start-block", "");
    this.endBlock = obj.getString("end-block", "");
    this.duration = obj.getInt("duration", 0);
    this.pause = obj.getInt("pause", 0);
    this.comment = obj.getString("comment", "");
  }

  /**
   * @return the startBlock
   */
  public String getStartBlock() {
    return this.startBlock;
  }

  /**
   * @param startBlock the startBlock to set
   */
  public void setStartBlock(String startBlock) {
    this.startBlock = startBlock;
  }

  /**
   * @return the endBlock
   */
  public String getEndBlock() {
    return this.endBlock;
  }

  /**
   * @param endBlock the endBlock to set
   */
  public void setEndBlock(String endBlock) {
    this.endBlock = endBlock;
  }

  /**
   * @return the duration
   */
  public int getDuration() {
    return this.duration;
  }

  /**
   * @param duration the duration to set
   */
  public void setDuration(int duration) {
    this.duration = duration;
  }

  /**
   * @return the pause
   */
  public int getPause() {
    return this.pause;
  }

  /**
   * @param pause the pause to set
   */
  public void setPause(int pause) {
    this.pause = pause;
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
