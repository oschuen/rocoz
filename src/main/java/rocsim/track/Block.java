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
package rocsim.track;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import rocsim.gui.tiles.Tile;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.TrackPlanModel.Direction;

public class Block {
  private List<Tile> tilesList = new ArrayList<>();

  public void addTile(Tile tile) {
    this.tilesList.add(tile);
  }

  public void addFirstTile(Tile tile) {
    this.tilesList.add(0, tile);
  }

  public boolean isEmpty() {
    return this.tilesList.isEmpty();
  }

  public void markBlocked() {
    for (Tile tile : this.tilesList) {
      tile.setState(UseState.BLOCK);
    }
  }

  public void markUnBlocked() {
    for (Tile tile : this.tilesList) {
      if (tile.getState() == UseState.BLOCK) {
        tile.setState(UseState.FREE);
      }
    }
  }

  public Optional<Tile> getNextTile(Tile tile) {
    int index = this.tilesList.indexOf(tile);
    if (index >= 0 && index + 1 < this.tilesList.size()) {
      return Optional.of(this.tilesList.get(index + 1));
    }
    return Optional.empty();
  }

  public void layBlock() {
    Tile k1 = null;
    Tile k2 = null;
    for (Tile tile : this.tilesList) {
      if (!(k1 == null || k2 == null)) {
        Direction fromDir = k2.getDirectionTo(k1);
        Direction toDir = k1.getDirectionTo(tile);
        k1.enableFollowUp(fromDir, toDir);
      }
      k2 = k1;
      k1 = tile;
    }
  }

  public int getDriveTime(int speed) {
    int time = 0;
    for (Tile tile : this.tilesList) {
      time += tile.getDrivingTime(Math.max(1, speed));
    }
    return Math.max(1, time);
  }
}
