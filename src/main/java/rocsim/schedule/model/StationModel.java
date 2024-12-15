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

public class StationModel {
  private String name = "";
  private List<PlatformModel> platforms = new ArrayList<>();

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

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("name", this.name);
    JsonArrayBuilder jPlatform = Json.createArrayBuilder();
    for (PlatformModel platformModel : this.platforms) {
      jPlatform.add(platformModel.toJson());
    }
    builder.add("platforms", jPlatform);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.name = obj.getString("name", "");
    this.platforms.clear();
    JsonArray jPlatform = obj.getJsonArray("platforms");
    if (jPlatform != null) {
      for (int i = 0; i < jPlatform.size(); i++) {
        PlatformModel platformModel = new PlatformModel();
        platformModel.fromJson(jPlatform.getJsonObject(i));
        addPlatform(platformModel);
      }
    }

  }

  public void addPlatform(PlatformModel model) {
    this.platforms.add(model);
  }

  public List<PlatformModel> getPlatforms() {
    return new ArrayList<>(this.platforms);
  }
}
