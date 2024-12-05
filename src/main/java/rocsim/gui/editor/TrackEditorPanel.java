package rocsim.gui.editor;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import javax.swing.JPanel;

import rocsim.gui.model.TileEditModel;
import rocsim.gui.model.TileEditModel.UndoListener;
import rocsim.gui.tiles.Tile;

public class TrackEditorPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private int raster = 40;
  private Point origin = new Point(0, 0);
  private MyMouseListener listener = new MyMouseListener();
  private Map<Point, Tile> tiles = new HashMap<>();
  private TileEditModel tileEditModel;
  private Deque<UndoAction> undos = new LinkedList<>();

  public TrackEditorPanel(TileEditModel model) {
    super();
    this.tileEditModel = model;
    this.addMouseMotionListener(this.listener);
    this.addMouseListener(this.listener);
    this.addMouseWheelListener(this.listener);
    model.addUndoListener(new UndoListener() {
      @Override
      public void undo() {
        UndoAction action = TrackEditorPanel.this.undos.pollFirst();
        if (action != null) {
          action.undo();
        }
      }
    });
  }

  private interface UndoAction {
    void undo();
  }

  @Override
  public void paint(Graphics arg0) {
    super.paint(arg0);
    Graphics2D gr = (Graphics2D) arg0;
    gr.translate(-this.raster * this.origin.x, -this.raster * this.origin.y);
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
      this.undos.offerFirst(new UndoAction() {
        @Override
        public void undo() {
          TrackEditorPanel.this.tiles.remove(tile.getLocation());
          triggerRepaint();
        }
      });
    } else {
      this.undos.offerFirst(new UndoAction() {
        @Override
        public void undo() {
          TrackEditorPanel.this.tiles.put(removeTile.getLocation(), removeTile);
          triggerRepaint();
        }
      });
    }
  }

  private class MyMouseListener implements MouseMotionListener, MouseListener, MouseWheelListener {

    private int x;
    private int y;
    private boolean move;

    @Override
    public void mouseDragged(MouseEvent e) {
      if (this.move) {
        int stepX = (this.x - e.getX()) / TrackEditorPanel.this.raster;
        int stepY = (this.y - e.getY()) / TrackEditorPanel.this.raster;
        TrackEditorPanel.this.origin.x += stepX;
        TrackEditorPanel.this.origin.y += stepY;
        this.x -= stepX * TrackEditorPanel.this.raster;
        this.y -= stepY * TrackEditorPanel.this.raster;
        triggerRepaint();
      }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
      boolean repaint = false;
      if (e.getButton() == MouseEvent.BUTTON1) {
        if (TrackEditorPanel.this.tileEditModel.isAddTileMode()) {
          Optional<Tile> t = TrackEditorPanel.this.tileEditModel.produceSelectedTile();
          t.ifPresent((tile) -> {
            TrackEditorPanel.this.addTile(tile,
                TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster,
                TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster);
          });
          repaint = true;
        } else if (TrackEditorPanel.this.tileEditModel.isDeleteTileMode()) {
          int px = (TrackEditorPanel.this.origin.x + e.getX() / TrackEditorPanel.this.raster);
          int py = (TrackEditorPanel.this.origin.y + e.getY() / TrackEditorPanel.this.raster);
          TrackEditorPanel.this.tiles.remove(new Point(px, py));
          repaint = true;
        }
      }
      if (repaint) {
        triggerRepaint();
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      this.move = e.getButton() == MouseEvent.BUTTON1;
      this.x = e.getX();
      this.y = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      this.move = false;
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
}
