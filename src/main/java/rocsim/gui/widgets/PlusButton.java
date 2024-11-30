package rocsim.gui.widgets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class PlusButton extends PaintedImageButton {
  private static final long serialVersionUID = 1L;

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    gr.setColor(Color.GRAY);
    gr.fillRect(0, 0, 20, 20);
    gr.setColor(Color.GREEN);
    gr.fillRect(2, 6, 16, 8);
    gr.fillRect(6, 2, 8, 16);
    return image;
  }

}
