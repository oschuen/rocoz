package rocsim.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class UndoButton extends PaintedImageButton {

  private static final long serialVersionUID = 1L;

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    BasicStroke thickStroke = new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    gr.setColor(Color.GRAY);
    gr.setStroke(thickStroke);
    gr.drawArc(10, 25, 40, 40, 50, 80);
    gr.drawLine(15, 28, 19, 20);
    gr.drawLine(15, 28, 24, 32);
    return image;
  }

}
