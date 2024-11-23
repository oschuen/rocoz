package rocsim.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Block extends Tile {

  private boolean stellBlock = false;

  public Block(String id, int x, int y, Direction orientation, boolean stellBlock) {
    super(id, x, y, orientation);
  }

  @Override
  public void innerDraw(int raster, Graphics2D g) {

    g.setColor(Color.PINK);
    g.fillRect(raster / 8, 0, 3 * raster / 4, raster);
    g.setColor(Color.BLACK);
    g.drawRect(raster / 8, 0, 3 * raster / 4, raster);
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

  @Override
  public Optional<Direction> getFollowUpDirection(Direction dir) {
    if (getOrientation().ordinal() % 2 == dir.ordinal() % 2) {
      return Optional.of(dir);
    }
    return Optional.empty();
  }

  /**
   * @return the stellBlock
   */
  public boolean isStellBlock() {
    return this.stellBlock;
  }
}
