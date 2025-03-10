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
package rocsim.gui.tiles;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import rocsim.schedule.model.TrackPlanModel.Direction;
import rocsim.schedule.model.TrackPlanModel.TrackKind;

public class Track extends Tile {

  public Track(String id, int x, int y, float length, Direction orientation) {
    super(id, x, y, length, orientation);
  }

  @Override
  public rocsim.schedule.model.TrackPlanModel.Track getTrack() {
    rocsim.schedule.model.TrackPlanModel.Track track = new rocsim.schedule.model.TrackPlanModel.Track();
    track.kind = TrackKind.TRACK;
    track.id = getId();
    track.location.x = getX();
    track.location.y = getY();
    track.length = getLength();
    track.orientation = getOrientation();
    return track;
  }

  @Override
  public void innerDraw(int raster, Graphics2D g) {

    g.setColor(Color.BLACK);
    g.fillRect(raster / 4, 0, raster / 2, raster);
    switch (getState()) {
    case BLOCK:
      g.setColor(Color.YELLOW);
      break;
    case FREE:
      g.setColor(Color.LIGHT_GRAY);
      break;
    case TRAIN:
      g.setColor(Color.RED);
      break;
    default:
      break;
    }
    g.fillRect(3 * raster / 8, raster / 4, raster / 4, raster / 2);
  }

  @Override
  public void enableFollowUp(Direction from, Direction to) {
    // do nothing
  }

  @Override
  public List<Direction> getPossibleDirections(Direction dir) {
    List<Direction> follows = new ArrayList<>();
    if (getOrientation().ordinal() % 2 == dir.ordinal() % 2) {
      follows.add(dir);
    }
    return follows;
  }

  public Direction[] getPossibleDirections() {
    return new Direction[] { Direction.values()[getOrientation().ordinal()],
        Direction.values()[(getOrientation().ordinal() + 2) % 4] };
  }

  @Override
  public Optional<Direction> getFollowUpDirection(Direction dir) {
    if (getOrientation().ordinal() % 2 == dir.ordinal() % 2) {
      return Optional.of(dir);
    }
    return Optional.empty();
  }

}
