package rocsim.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

public class Track extends Tile {

  public Track(int x, int y, Direction orientation) {
    super(x, y, orientation);
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
