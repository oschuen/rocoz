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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import rocsim.schedule.model.TrackPlanModel.Direction;
import rocsim.schedule.model.TrackPlanModel.Track;
import rocsim.schedule.model.TrackPlanModel.TrackKind;

public class Curve extends Tile {

  public Curve(String id, int x, int y, Direction orientation) {
    super(id, x, y, orientation);
  }

  @Override
  public Track getTrack() {
    Track track = new Track();
    track.kind = TrackKind.CURVE;
    track.id = getId();
    track.location.x = getX();
    track.location.y = getY();
    track.length = getLength();
    track.orientation = getOrientation();
    return track;
  }

  @Override
  protected void innerDraw(int raster, Graphics2D g) {
    g.setColor(Color.black);
    int x[] = new int[] { raster / 4, 3 * raster / 4, raster, raster };
    int y[] = new int[] { raster, raster, 3 * raster / 4, raster / 4 };
    g.fillPolygon(x, y, 4);
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
    g.translate(3 * raster / 4, 3 * raster / 4);
    g.rotate(Math.toRadians(-45));
    g.fillRect(-raster / 6, -raster / 16, raster / 3, raster / 8);
  }

  @Override
  public void enableFollowUp(Direction from, Direction to) {
    // do nothing
  }

  @Override
  public List<Direction> getPossibleDirections(Direction dir) {
    List<Direction> follows = new ArrayList<>();
    if (getOrientation().ordinal() == (dir.ordinal() + 2) % 4) {
      follows.add(Direction.values()[(dir.ordinal() + 1) % 4]);
    } else if (getOrientation().ordinal() == (dir.ordinal() + 3) % 4) {
      follows.add(getOrientation());
    }
    return follows;
  }

  @Override
  public Optional<Direction> getFollowUpDirection(Direction dir) {
    if (getOrientation().ordinal() == (dir.ordinal() + 2) % 4) {
      return Optional.of(Direction.values()[(dir.ordinal() + 1) % 4]);
    } else if (getOrientation().ordinal() == (dir.ordinal() + 3) % 4) {
      return Optional.of(getOrientation());
    }
    return Optional.empty();
  }

  public static void main(String[] args) {
    BufferedImage br = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    Curve curve = new Curve("", 3, 4, Direction.NORTH);
    Graphics2D gr = br.createGraphics();
    gr.setColor(Color.WHITE);
    gr.fillRect(1, 1, 398, 398);

    gr.setColor(Color.BLACK);
    gr.drawRect(0, 0, 40, 40);

    curve.setX(1);
    curve.setY(1);
    curve.setOrientation(Direction.NORTH);
    curve.setState(UseState.BLOCK);
    curve.draw(40, gr);

    curve.setX(3);
    curve.setY(1);
    curve.setOrientation(Direction.WEST);
    curve.setState(UseState.TRAIN);
    curve.draw(40, gr);

    curve.setX(5);
    curve.setY(1);
    curve.setOrientation(Direction.SOUTH);
    curve.setState(UseState.FREE);
    curve.draw(40, gr);

    curve.setX(7);
    curve.setY(1);
    curve.setOrientation(Direction.EAST);
    curve.draw(40, gr);

    try {
      ImageIO.write(br, "PNG", new File("track.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
