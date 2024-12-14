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
import rocsim.schedule.model.TrackPlanModel.Track;
import rocsim.schedule.model.TrackPlanModel.TrackKind;

public class RightSwitch extends Tile {

  public RightSwitch(String id, int x, int y, Direction orientation) {
    super(id, x, y, orientation);
  }

  @Override
  public Track getTrack() {
    Track track = new Track();
    track.kind = TrackKind.RIGHT_SWICH;
    track.id = getId();
    track.location.x = getX();
    track.location.y = getY();
    track.length = getLength();
    track.orientation = getOrientation();
    return track;
  }

  protected void innerDrawSwitch(int raster, Graphics2D g) {
    g.setColor(Color.black);
    int x[] = new int[] { 3 * raster / 4, raster / 4, 0, 0 };
    int y[] = new int[] { 0, 0, raster / 4, 3 * raster / 4 };
    g.fillPolygon(x, y, 4);
    if (this.switched) {
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
        g.setColor(Color.GREEN);
        break;
      }
    } else {
      g.setColor(Color.DARK_GRAY);
    }
    g.translate(raster / 4, raster / 4);
    g.rotate(Math.toRadians(-45));
    g.fillRect(-raster / 6, -raster / 16, raster / 3, raster / 8);
  }

  protected void innerDrawStraight(int raster, Graphics2D g) {

    g.setColor(Color.BLACK);
    g.fillRect(raster / 4, 0, raster / 2, raster);
    if (this.switched) {
      g.setColor(Color.DARK_GRAY);
    } else {
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
        g.setColor(Color.GREEN);
        break;
      }

    }
    g.fillRect(3 * raster / 8, raster / 4, raster / 4, raster / 2);
  }

  @Override
  public void enableFollowUp(Direction from, Direction to) {
    List<Direction> possibles = getPossibleDirections(from);
    if (possibles.contains(to)) {
      getFollowUpDirection(from).ifPresentOrElse(possible -> {
        if (possible != to) {
          this.switched = !this.switched;
        }
      }, () -> {
        this.switched = !this.switched;
      });
    }
  }

  @Override
  protected void innerDraw(int raster, Graphics2D g) {
    if (this.switched) {
      innerDrawStraight(raster, (Graphics2D) g.create());
      innerDrawSwitch(raster, (Graphics2D) g.create());
    } else {
      innerDrawSwitch(raster, (Graphics2D) g.create());
      innerDrawStraight(raster, (Graphics2D) g.create());
    }

  }

  @Override
  public List<Direction> getPossibleDirections(Direction dir) {
    List<Direction> follows = new ArrayList<>();

    if (getOrientation().ordinal() == dir.ordinal()) {
      follows.add(Direction.values()[(dir.ordinal() + 1) % 4]);
    }
    if (getOrientation().ordinal() == (dir.ordinal() + 1) % 4) {
      follows.add(Direction.values()[(dir.ordinal() + 3) % 4]);
    }
    if (getOrientation().ordinal() % 2 == dir.ordinal() % 2) {
      follows.add(dir);
    }
    return follows;
  }

  @Override
  public Optional<Direction> getFollowUpDirection(Direction dir) {
    if (this.switched) {
      if (getOrientation().ordinal() == dir.ordinal()) {
        return Optional.of(Direction.values()[(dir.ordinal() + 1) % 4]);
      } else if (getOrientation().ordinal() == (dir.ordinal() + 1) % 4) {
        return Optional.of(Direction.values()[(dir.ordinal() + 3) % 4]);
      }
    } else {
      if (getOrientation().ordinal() % 2 == dir.ordinal() % 2) {
        return Optional.of(dir);
      }
    }
    return Optional.empty();

  }

  /**
   * @return the switched
   */
  public boolean isSwitched() {
    return this.switched;
  }

  /**
   * @param switched the switched to set
   */
  public void setSwitched(boolean switched) {
    this.switched = switched;
  }

  private boolean switched = false;
}
