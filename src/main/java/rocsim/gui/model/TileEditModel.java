package rocsim.gui.model;

import java.util.Optional;

import rocsim.gui.tiles.Tile;

public interface TileEditModel {
  interface TileModelListener {
    void undo();

    void unselect();
  }

  boolean isAddTileMode();

  boolean isDeleteTileMode();

  boolean isSelectionMoveMode();

  Optional<Tile> produceSelectedTile();

  public void addModelListener(TileModelListener listener);

  public void removeModelListener(TileModelListener listener);
}
