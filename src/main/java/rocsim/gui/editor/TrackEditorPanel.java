package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import rocsim.gui.tiles.Tile;
import rocsim.gui.widgets.TileSelectionPanel;

public class TrackEditorPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private Map<Point, Tile> tiles = new HashMap<>();
  private PlanPanel planPannel;
  private TileSelectionPanel panel;

  private class PlanPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private int raster = 40;
    private Point origin = new Point(0, 0);
    private MyMouseListener listener = new MyMouseListener();

    public PlanPanel() {
      super();
      this.addMouseMotionListener(this.listener);
      this.addMouseListener(this.listener);
      this.addMouseWheelListener(this.listener);
      this.addKeyListener(new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
          System.out.println("pressed");
        }
      });
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

    private class MyMouseListener implements MouseMotionListener, MouseListener, MouseWheelListener {

      private int x;
      private int y;
      private boolean move;

      @Override
      public void mouseDragged(MouseEvent e) {
        if (this.move) {
          int stepX = (this.x - e.getX()) / PlanPanel.this.raster;
          int stepY = (this.y - e.getY()) / PlanPanel.this.raster;
          PlanPanel.this.origin.x += stepX;
          PlanPanel.this.origin.y += stepY;
          this.x -= stepX * PlanPanel.this.raster;
          this.y -= stepY * PlanPanel.this.raster;
          javax.swing.SwingUtilities.invokeLater(() -> {
            repaint();
          });
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        System.out.println("Clicked");
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
        int x = PlanPanel.this.origin.x + e.getX() / PlanPanel.this.raster;
        int y = PlanPanel.this.origin.y + e.getY() / PlanPanel.this.raster;
        PlanPanel.this.raster = Math.min(100, Math.max(10, PlanPanel.this.raster + e.getWheelRotation() * 10));
        PlanPanel.this.origin.x = x - e.getX() / PlanPanel.this.raster;
        PlanPanel.this.origin.y = y - e.getY() / PlanPanel.this.raster;
        javax.swing.SwingUtilities.invokeLater(() -> {
          repaint();
        });
      }
    }

  }

  public TrackEditorPanel() {
    this.planPannel = new PlanPanel();
    setLayout(new BorderLayout(0, 0));

    this.panel = new TileSelectionPanel();
    add(this.panel, BorderLayout.NORTH);
    add(this.planPannel, BorderLayout.CENTER);
  }

}
