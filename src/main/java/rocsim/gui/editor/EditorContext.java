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
package rocsim.gui.editor;

import java.util.List;

import rocsim.schedule.model.LineModel;
import rocsim.schedule.model.StationModel;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TripModel;
import rocsim.track.Block;

public interface EditorContext {
  List<String> getArrivableBlockIds();

  List<String> getWatchBlockIds();

  List<String> getStationNames();

  List<String> getPlatforms(String station);

  List<String> getLineNames();

  TimeModel getTimeModel();

  List<String> getLocoIds();

  String getBlockForPlatform(String station, String platform);

  Block getBlock(String blockIdStart, String blockIdEnd);

  java.util.Map.Entry<String, String> getStationAndPlatform(String blockId);

  LineModel getLineModel(String line);

  List<LineModel> getLineModels();

  List<StationModel> getStationModels();

  StationModel getStationModel(String station);

  List<TripModel> getTripModels();

  TripModel getTripModel(String tripId);

  void updateModel(String tripId, TripModel updatedModel);

  void addModel(TripModel updatedModel);

}
