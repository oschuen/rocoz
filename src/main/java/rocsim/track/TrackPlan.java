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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rocsim.gui.tiles.Tile;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.TrackPlanModel.Direction;

public class TrackPlan {
  private List<Tile> tilesList;
  private Map<Point, Tile> tiles = new HashMap<>();

  public TrackPlan() {
    super();
    this.tilesList = new ArrayList<>();
  }

  public void setTilesList(List<Tile> tilesList) {
    this.tilesList.clear();
    this.tiles.clear();
    this.tilesList = tilesList;
    for (Tile tile : tilesList) {
      Point p = new Point(tile.getX(), tile.getY());
      this.tiles.put(p, tile);
    }
  }

  public Tile getTile(Point p) {
    return this.tiles.get(p);
  }

  public Tile getTile(String id) {
    Tile result = null;
    for (Tile tile : this.tilesList) {
      if (tile.getId().equals(id)) {
        result = tile;
        break;
      }
    }

    return result;
  }

  public void reset() {
    for (Tile tile : this.tilesList) {
      tile.setState(UseState.FREE);
    }
  }

  private Tile getTileInDirection(Tile tile, Direction dir) {
    switch (dir) {
    case EAST:
      return getTile(new Point(tile.getX() + 1, tile.getY()));
    case NORTH:
      return getTile(new Point(tile.getX(), tile.getY() - 1));
    case SOUTH:
      return getTile(new Point(tile.getX(), tile.getY() + 1));
    case WEST:
      return getTile(new Point(tile.getX() - 1, tile.getY()));
    default:
      return null;
    }
  }

  private void buildBlock(Block block, Tile from, Tile to, Direction dir, boolean ignoreFirstState) {
    List<Direction> dirs = from.getPossibleDirections(dir);
    boolean fromAllowed = ignoreFirstState || from.getState() == UseState.FREE;
    for (Iterator<Direction> iterator = dirs.iterator(); fromAllowed && block.isEmpty() && iterator.hasNext();) {
      Direction direction = iterator.next();
      Tile follow = getTileInDirection(from, direction);
      if (follow != null) {
        if (follow == to) {
          block.addTile(to);
          break;
        } else {
          buildBlock(block, follow, to, direction, false);
        }
      }
    }
    if (!block.isEmpty()) {
      block.addFirstTile(from);
    }
  }

  public Block getBlock(Tile from, Tile to) {
    Block result = new Block();
    for (int i = 0; result.isEmpty() && i < 4; i++) {
      buildBlock(result, from, to, Direction.values()[i], true);
    }

    return result;
  }
}
