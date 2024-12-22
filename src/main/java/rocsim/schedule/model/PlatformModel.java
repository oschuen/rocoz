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

public class PlatformModel {
  private String name = "";
  private String blockId;
  private boolean shuntingBlock = false;

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("name", this.name);
    builder.add("block-id", this.blockId);
    builder.add("shunting-block", this.shuntingBlock);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.name = obj.getString("name", "");
    this.blockId = obj.getString("block-id", "");
    this.shuntingBlock = obj.getBoolean("shunting-block", false);
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

  /**
   * @return the shuntingBlock
   */
  public boolean isShuntingBlock() {
    return this.shuntingBlock;
  }

  /**
   * @param shuntingBlock the shuntingBlock to set
   */
  public void setShuntingBlock(boolean shuntingBlock) {
    this.shuntingBlock = shuntingBlock;
  }

}
