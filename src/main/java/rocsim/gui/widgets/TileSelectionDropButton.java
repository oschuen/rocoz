package rocsim.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class TileSelectionDropButton extends PaintedImageButton {
  private static final long serialVersionUID = 1L;

  public TileSelectionDropButton() {
    setBackground(Color.WHITE);
  }

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    BasicStroke thickStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    gr.setStroke(thickStroke);

    gr.setColor(Color.GRAY);
    int[] x = new int[] { 5, 15, 45, 55 };
    int[] y = new int[] { 55, 48, 48, 55 };
    gr.drawPolygon(x, y, 4);
    int[] xx = new int[] { 5, 15, 45, 55 };
    int[] yy = new int[] { 47, 40, 40, 47 };
    gr.setColor(Color.DARK_GRAY);
    gr.drawPolygon(xx, yy, 4);
    gr.setColor(Color.GREEN);
    gr.fillPolygon(xx, yy, 4);
    gr.setColor(Color.GRAY);
    gr.drawLine(30, 8, 30, 43);
    gr.drawLine(30, 43, 20, 33);
    gr.drawLine(30, 43, 40, 33);

    return image;
  }

}
