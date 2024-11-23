package rocsim.track;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.Tile;
import rocsim.gui.Tile.Direction;
import rocsim.gui.Tile.UseState;

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
}