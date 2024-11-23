package rocsim.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPanel;

import rocsim.track.TrackPlan;

public class PlanPannel extends JPanel {

  private static final long serialVersionUID = 1L;

  private Point origin = new Point(0, 0);
  private int raster = 40;
  private TrackPlan plan;

  public PlanPannel(TrackPlan plan) {
    super();
    this.plan = plan;

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int prefWidth = Math.max(6 * screenSize.width / 8, (this.plan.getDimension().width + 1) * this.raster);
    int prefHeight = Math.max(6 * screenSize.height / 8, (this.plan.getDimension().height + 1) * this.raster);

    setPreferredSize(new Dimension(prefWidth, prefHeight));
//    Tile tile = this.plan.getTile("sb1");
//    tile.setState(UseState.TRAIN);
//    rocsim.track.Block block = this.plan.getBlock(this.plan.getTile("sb1"), this.plan.getTile("sb3"));
//    block.markBlocked();
//    block.layBlock();

//    rocsim.track.Block block2 = this.plan.getBlock(this.plan.getTile("bk3"), this.plan.getTile("bk1"));
//    block2.markBlocked();
//    block2.layBlock();
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
        Tile tile = this.plan.getTile(new Point(this.origin.x + x, this.origin.y + y));
        if (tile != null) {
          tile.draw(this.raster, gr);
        }
      }
    }
  }
}
