package rocsim.track;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rocsim.gui.tiles.Tile;
import rocsim.gui.tiles.Tile.Direction;
import rocsim.gui.tiles.Tile.UseState;

public class TrackPlan {
  private List<Tile> tilesList;
  private Map<Point, Tile> tiles = new HashMap<>();

  public TrackPlan(List<Tile> tilesList) {
    super();
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

  private Tile getTileInDirection(Tile tile, Tile.Direction dir) {
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

  private void buildBlock(Block block, Tile from, Tile to, Tile.Direction dir, boolean ignoreFirstState) {
    List<Tile.Direction> dirs = from.getPossibleDirections(dir);
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
      buildBlock(result, from, to, Tile.Direction.values()[i], true);
    }

    return result;
  }
}
