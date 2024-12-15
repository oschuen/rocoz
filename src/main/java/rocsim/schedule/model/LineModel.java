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

public class LineModel {
  private String name;
  private List<LineSegmentModel> segments = new ArrayList<>();

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add("name", this.name);
    JsonArrayBuilder jSegment = Json.createArrayBuilder();
    for (LineSegmentModel segmentModel : this.segments) {
      jSegment.add(segmentModel.toJson());
    }
    builder.add("segments", jSegment);
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    this.name = obj.getString("name", "");
    this.segments.clear();
    JsonArray jPlatform = obj.getJsonArray("segments");
    if (jPlatform != null) {
      for (int i = 0; i < jPlatform.size(); i++) {
        LineSegmentModel segmentModel = new LineSegmentModel();
        segmentModel.fromJson(jPlatform.getJsonObject(i));
        addSegment(segmentModel);
      }
    }

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

  public void addSegment(LineSegmentModel model) {
    this.segments.add(model);
  }

  public List<LineSegmentModel> getLineSegments() {
    return new ArrayList<>(this.segments);
  }
}
