package rocsim.gui.tiles;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.List;
import java.util.Optional;

public abstract class Tile {

  public static enum Direction {
    SOUTH, WEST, NORTH, EAST
  }

  public static enum UseState {
    FREE, BLOCK, TRAIN
  }

  public Tile(String id, int x, int y, Direction orientation) {
    super();
    this.id = id;
    this.location.x = x;
    this.location.y = y;
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

  public abstract void enableFollowUp(Direction from, Direction to);

  public abstract List<Direction> getPossibleDirections(Direction dir);

  public abstract Optional<Direction> getFollowUpDirection(Direction dir);

  /**
   * @param x the x to set
   */
  public void setX(int x) {
    this.location.x = x;
  }

  /**
   * @param y the y to set
   */
  public void setY(int y) {
    this.location.y = y;
  }

  /**
   * @return the x
   */
  public int getX() {
    return this.location.x;
  }

  /**
   * @return the y
   */
  public int getY() {
    return this.location.y;
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

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @param id the id to set
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * @return the location
   */
  public Point getLocation() {
    return new Point(this.location);
  }

  /**
   * @param location the location to set
   */
  public void setLocation(Point location) {
    this.location = location;
  }

  public int getDrivingTime(int speed) {
    if (speed > 0) {
      return (int) (0.087F / speed * 3600.0F) + 1;
    }
    return 3600;
  }

  public Direction getDirectionTo(Tile other) {
    if (other.location.y < this.location.y) {
      return Direction.NORTH;
    } else if (other.location.y > this.location.y) {
      return Direction.SOUTH;
    } else if (other.location.x < this.location.x) {
      return Direction.WEST;
    }
    return Direction.EAST;
  }

  private String id = "";
  private Direction orientation = Direction.NORTH;
  private UseState state = UseState.FREE;
  private Point location = new Point(0, 0);
}