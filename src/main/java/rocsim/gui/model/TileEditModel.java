package rocsim.gui.model;

import java.util.Optional;

import rocsim.gui.tiles.Tile;

public interface TileEditModel {
  interface UndoListener {
    void undo();
  }

  boolean isAddTileMode();

  boolean isDeleteTileMode();

  Optional<Tile> produceSelectedTile();

  public void addUndoListener(UndoListener listener);

  public void removeUndoListener(UndoListener listener);
}
