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
package rocsim.gui.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.swing.JPanel;

import rocsim.gui.model.SelectionModel;
import rocsim.gui.model.TileEditModel;
import rocsim.gui.model.TileEditModel.TileModelListener;
import rocsim.gui.tiles.Tile;
import rocsim.schedule.model.TrackPlanModel;

public class TrackEditorPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private int raster = 40;
  private Point origin = new Point(0, 0);
  private MyMouseListener listener = new MyMouseListener();
  private Map<Point, Tile> tiles = new HashMap<>();
  private TileEditModel tileEditModel;
  private Deque<UndoAction> undos = new LinkedList<>();
  private SelectionModel selectionModel = new SelectionModel();

  public TrackEditorPanel(TileEditModel model) {
    super();
    this.tileEditModel = model;
    this.addMouseMotionListener(this.listener);
    this.addMouseListener(this.listener);
    this.addMouseWheelListener(this.listener);
    model.addModelListener(new TileModelListener() {

      @Override
      public void undo() {
        UndoAction action = TrackEditorPanel.this.undos.pollFirst();
        if (action != null) {
          action.undo();
        }
      }

      @Override
      public void unselect() {
        TrackEditorPanel.this.selectionModel.setSelected(false);
        TrackEditorPanel.this.selectionModel.setDragging(false);
        triggerRepaint();
      }

      @Override
      public void dropSelection() {
        if (TrackEditorPanel.this.selectionModel.isSelected()) {
          moveSelection();
        }
      }
    });
    setToolTipText("");
  }

  private interface UndoAction {
    void undo();
  }

  private class MoveBackTileAction implements UndoAction {
    private List<Tile> movable;
    private int deltaX;
    private int deltaY;

    public MoveBackTileAction(List<Tile> movable, int deltaX, int deltaY) {
      super();
      this.movable = movable;
      this.deltaX = deltaX;
      this.deltaY = deltaY;
    }

    @Override
    public void undo() {
      for (Tile tile : this.movable) {
        TrackEditorPanel.this.tiles.remove(tile.getLocation());
        Point newLocation = new Point(tile.getLocation().x - this.deltaX, tile.getLocation().y - this.deltaY);
        tile.setLocation(newLocation);
        TrackEditorPanel.this.tiles.put(newLocation, tile);
      }
      triggerRepaint();
    }
  }

  private class RestoreTileAction implements UndoAction {
    private List<Tile> restorable = new ArrayList<>();

    public RestoreTileAction() {

    }

    public RestoreTileAction(Tile tile) {
      addTile(tile);
    }

    public void addTile(Tile tile) {
      this.restorable.add(tile);
    }

    @Override
    public void undo() {
      for (Tile tile : this.restorable) {
        TrackEditorPanel.this.tiles.put(tile.getLocation(), tile);
      }
      triggerRepaint();
    }
  }

  private class CompositeAction implements UndoAction {
    private List<UndoAction> actions = new ArrayList<>();

    @Override
    public void undo() {
      for (UndoAction undoAction : this.actions) {
        undoAction.undo();
      }
    }

    public void addAction(UndoAction action) {
      this.actions.add(action);
    }
  }

  private void pushUndoAction(UndoAction undo) {
    this.undos.offerFirst(undo);
    while (this.undos.size() > 20) {
      this.undos.pollLast();
    }
  }

  @Override
  public String getToolTipText(MouseEvent e) {
    Point mouseLoc = new Point(this.origin.x + e.getX() / this.raster, this.origin.y + e.getY() / this.raster);
    Tile tile = this.tiles.get(mouseLoc);
    if (tile != null) {
      return tile.getId();
    }
    return null;
  }

  @Override
  public void paint(Graphics arg0) {
    super.paint(arg0);
    Graphics2D gr = (Graphics2D) arg0;
    gr.translate(-this.raster * this.origin.x, -this.raster * this.origin.y);
    if (this.selectionModel.isSelected() || this.selectionModel.isDragging()) {
      gr.setColor(Color.GREEN);
      Rectangle source = this.selectionModel.getSource();
      Point dest = this.selectionModel.getDest();
      gr.drawRect(source.x * this.raster, source.y * this.raster, source.width * this.raster,
          source.height * this.raster);
      gr.fillRect(dest.x * this.raster, dest.y * this.raster, source.width * this.raster, source.height * this.raster);
    }
    int xw = getWidth() / this.raster + 1;
    int yw = getHeight() / this.raster + 1;
    for (int x = 0; x < xw; x++) {
      for (int y = 0; y < yw; y++) {
        Tile tile = TrackEditorPanel.this.tiles.get(new Point(this.origin.x + x, this.origin.y + y));
        if (tile != null) {
          tile.draw(this.raster, gr);
        }
      }
    }
  }

  private void triggerRepaint() {
    javax.swing.SwingUtilities.invokeLater(() -> {
      repaint();
    });
  }

  private void addTile(Tile tile, int x, int y) {
    tile.setX(x);
    tile.setY(y);
    Tile removeTile = this.tiles.remove(tile.getLocation());
    this.tiles.put(tile.getLocation(), tile);
    if (removeTile == null) {
      pushUndoAction(new UndoAction() {
        @Override
        public void undo() {
          TrackEditorPanel.this.tiles.remove(tile.getLocation());
          triggerRepaint();
        }
      });
    } else {
      pushUndoAction(new RestoreTileAction(removeTile));
    }
    triggerRepaint();
  }

  private void removeTile(int x, int y) {
    Tile removed = this.tiles.remove(new Point(x, y));
    if (removed != null) {
      pushUndoAction(new RestoreTileAction(removed));
    }
    triggerRepaint();
  }

  private void moveSelection() {
    RestoreTileAction restoreAction = new RestoreTileAction();
    List<Tile> moveTiles = new ArrayList<>();
    for (int x = 0; x < this.selectionModel.getSource().width; x++) {
      for (int y = 0; y < this.selectionModel.getSource().height; y++) {
        Point destP = new Point(this.selectionModel.getDest().x + x, this.selectionModel.getDest().y + y);
        Point sourceP = new Point(this.selectionModel.getSource().x + x, this.selectionModel.getSource().y + y);
        if (!this.selectionModel.isInSourceSelection(destP) && this.tiles.containsKey(destP)) {
          Tile remove = this.tiles.remove(destP);
          restoreAction.addTile(remove);
        }
        if (this.tiles.containsKey(sourceP)) {

          moveTiles.add(this.tiles.get(sourceP));
        }
      }
    }
    int deltaX = this.selectionModel.getDest().x - this.selectionModel.getSource().x;
    int deltaY = this.selectionModel.getDest().y - this.selectionModel.getSource().y;
    for (Tile tile : moveTiles) {
      this.tiles.remove(tile.getLocation());
      Point newLocation = new Point(tile.getLocation().x + deltaX, tile.getLocation().y + deltaY);
      tile.setLocation(newLocation);
      this.tiles.put(newLocation, tile);
    }
    triggerRepaint();
    MoveBackTileAction moveBack = new MoveBackTileAction(moveTiles, deltaX, deltaY);
    CompositeAction composite = new CompositeAction();
    composite.addAction(moveBack);
    composite.addAction(restoreAction);
    pushUndoAction(composite);
  }

  private class MyMouseListener implements MouseMotionListener, MouseListener, MouseWheelListener {

    private int x;
    private int y;
    private boolean move;
    private boolean select;
    private boolean selectionMove;

    private void drag(Point mp) {
      int stepX = (this.x - mp.x) / TrackEditorPanel.this.raster;
      int stepY = (this.y - mp.y) / TrackEditorPanel.this.raster;
      if (this.selectionMove) {
        Point currentDest = TrackEditorPanel.this.selectionModel.getDest();
        currentDest.x -= stepX;
        currentDest.y -= stepY;
        TrackEditorPanel.this.selectionModel.setDest(currentDest);
        triggerRepaint();
      } else if (this.move) {
        TrackEditorPanel.this.origin.x += stepX;
        TrackEditorPanel.this.origin.y += stepY;
        triggerRepaint();
      }
      this.x -= stepX * TrackEditorPanel.this.raster;
      this.y -= stepY * TrackEditorPanel.this.raster;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (this.select) {
        if (TrackEditorPanel.this.selectionModel.isDragging()) {
          Rectangle source = TrackEditorPanel.this.selectionModel.getSource();
          int xx = 1 + TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster;
          int yy = 1 + TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster;
          source.width = xx - source.x;
          source.height = yy - source.y;
          if (source.width >= 1 && source.height >= 1) {
            TrackEditorPanel.this.selectionModel.setSource(source);
          }
          triggerRepaint();
        } else {
          int xx = TrackEditorPanel.this.origin.x + this.x / TrackEditorPanel.this.raster;
          int yy = TrackEditorPanel.this.origin.y + this.y / TrackEditorPanel.this.raster;
          Rectangle source = new Rectangle(xx, yy, 1, 1);
          TrackEditorPanel.this.selectionModel.setSource(source);
          TrackEditorPanel.this.selectionModel.setDest(new Point(xx, yy));
          TrackEditorPanel.this.selectionModel.setDragging(true);
          triggerRepaint();
        }
      } else {
        drag(e.getPoint());
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        if (TrackEditorPanel.this.tileEditModel.isAddTileMode()) {
          Optional<Tile> t = TrackEditorPanel.this.tileEditModel.produceSelectedTile();
          t.ifPresent((tile) -> {
            TrackEditorPanel.this.addTile(tile,
                TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster,
                TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster);
          });
        } else if (TrackEditorPanel.this.tileEditModel.isDeleteTileMode()) {
          int px = (TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster);
          int py = (TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster);
          removeTile(px, py);
        }
      } else if (e.getButton() == MouseEvent.BUTTON3) {
        int px = (TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster);
        int py = (TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster);
        Tile tile = TrackEditorPanel.this.tiles.get(new Point(px, py));
        if (tile != null) {
          ConfigureTileDialog configureDialog = new ConfigureTileDialog(tile);
          configureDialog.setLocation(e.getLocationOnScreen());
          configureDialog.setVisible(true);
        }
      }

    }

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.getButton() == MouseEvent.BUTTON1) {
        int xx = TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster;
        int yy = TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster;

        if (TrackEditorPanel.this.tileEditModel.isSelectionMoveMode()
            && !TrackEditorPanel.this.selectionModel.isSelected()) {
          this.select = true;
        } else if (TrackEditorPanel.this.tileEditModel.isSelectionMoveMode()
            && TrackEditorPanel.this.selectionModel.isSelected()
            && TrackEditorPanel.this.selectionModel.isInDestSelection(new Point(xx, yy))) {
          this.selectionMove = true;
        } else {
          this.move = true;
        }

        this.x = e.getX();
        this.y = e.getY();
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      if (this.select && TrackEditorPanel.this.selectionModel.isDragging()) {
        TrackEditorPanel.this.selectionModel.setSelected(true);
        TrackEditorPanel.this.selectionModel.setDragging(false);
      }
      this.move = false;
      this.select = false;
      this.selectionMove = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
      int x = TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster;
      int y = TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster;
      TrackEditorPanel.this.raster = Math.min(100,
          Math.max(10, TrackEditorPanel.this.raster + e.getWheelRotation() * 10));
      TrackEditorPanel.this.origin.x = x - e.getX() / TrackEditorPanel.this.raster;
      TrackEditorPanel.this.origin.y = y - e.getY() / TrackEditorPanel.this.raster;
      triggerRepaint();
    }
  }

  public void setTrackModel(TrackPlanModel trackModel) {
    this.tiles.clear();
    for (Tile tile : trackModel.generateTiles()) {
      this.tiles.put(tile.getLocation(), tile);
    }
  }

  public TrackPlanModel getTrackModel() {
    TrackPlanModel trackModel = new TrackPlanModel();
    for (Entry<Point, Tile> pair : this.tiles.entrySet()) {
      trackModel.addTrack(pair.getValue().getTrack());
    }
    return trackModel;
  }

}
