package rocsim.gui.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PlayButton extends PaintedImageButton {

  private static final long serialVersionUID = 1L;

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    gr.setColor(Color.GRAY);
    gr.fillRect(0, 0, 100, 100);
    int x[] = new int[] { 20, 80, 20 };
    int y[] = new int[] { 20, 50, 80 };
    gr.setColor(Color.GREEN);
    gr.fillPolygon(x, y, 3);
    return image;
  }
}
