package rocsim.gui;

import java.awt.Graphics2D;
import java.util.Optional;

public abstract class Tile {

  public static enum Direction {
    SOUTH, WEST, NORTH, EAST
  }

  public static enum UseState {
    FREE, BLOCK, TRAIN
  }

  public Tile(int x, int y, Direction orientation) {
    super();
    this.x = x;
    this.y = y;
    this.orientation = orientation;
  }

  protected abstract void innerDraw(int raster, Graphics2D g);

  public void draw(int raster, Graphics2D g) {
    final int scaler = 100;
    Graphics2D subGraphics = (Graphics2D) g.create(this.getX() * raster, this.getY() * raster, raster + 1, raster + 1);
    subGraphics.rotate(Math.toRadians(90.0 * getOrientation().ordinal()));
    switch (getOrientation()) {
    case EAST:
      subGraphics.translate(-raster, 0);
      break;
    case NORTH:
      subGraphics.translate(-raster, -raster);
      break;
    case WEST:
      subGraphics.translate(0, -raster);
      break;
    case SOUTH:
    default:
      break;
    }
    subGraphics.scale(raster / (double) scaler, raster / (double) scaler);
    innerDraw(scaler, subGraphics);
  }

  public abstract Direction[] getPossibleDirections();

  public abstract Optional<Direction> getFollowUpDirection(Direction dir);

  /**
   * @param x the x to set
   */
  public void setX(int x) {
    this.x = x;
  }

  /**
   * @param y the y to set
   */
  public void setY(int y) {
    this.y = y;
  }

  /**
   * @return the x
   */
  public int getX() {
    return this.x;
  }

  /**
   * @return the y
   */
  public int getY() {
    return this.y;
  }

  /**
   * @return the orientation
   */
  public Direction getOrientation() {
    return this.orientation;
  }

  /**
   * @param orientation the orientation to set
   */
  public void setOrientation(Direction orientation) {
    this.orientation = orientation;
  }

  /**
   * @return the state
   */
  public UseState getState() {
    return this.state;
  }

  /**
   * @param state the state to set
   */
  public void setState(UseState state) {
    this.state = state;
  }

  private int x;
  private int y;
  private Direction orientation = Direction.NORTH;
  private UseState state = UseState.FREE;
}
