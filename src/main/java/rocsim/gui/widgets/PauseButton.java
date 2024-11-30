package rocsim.gui.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PauseButton extends PaintedImageButton {
  private static final long serialVersionUID = 1L;

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    gr.setColor(Color.GRAY);
    gr.fillRect(0, 0, 100, 100);
    gr.setColor(Color.RED);
    gr.fillRect(20, 20, 20, 60);
    gr.fillRect(60, 20, 20, 60);
    return image;
  }
}
