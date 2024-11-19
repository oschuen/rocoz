package rocsim.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public class PlanPannel extends JPanel {

  private static final long serialVersionUID = 1L;

  private Map<Point, Tile> tiles = new HashMap<>();

  private Point origin = new Point(0, 0);
  private int raster = 40;

  public PlanPannel(List<Tile> tilesList) {
    super();
    int maxX = 0;
    int maxY = 0;
    for (Tile tile : tilesList) {
      Point p = new Point(tile.getX(), tile.getY());
      this.tiles.put(p, tile);
      maxX = Math.max(maxX, tile.getX() + 1);
      maxY = Math.max(maxY, tile.getY() + 1);
    }
    setPreferredSize(new Dimension(maxX * this.raster, maxY * this.raster));
  }

  @Override
  public void paint(Graphics arg0) {
    super.paint(arg0);
    Graphics2D gr = (Graphics2D) arg0;
    gr.translate(-this.raster * this.origin.x, -this.raster * this.origin.y);
    int xw = getWidth() / this.raster;
    int yw = getHeight() / this.raster;
    for (int x = 0; x < xw; x++) {
      for (int y = 0; y < yw; y++) {
        Tile tile = this.tiles.get(new Point(this.origin.x + x, this.origin.y + y));
        if (tile != null) {
          tile.draw(this.raster, gr);
        }
      }
    }
  }

}
