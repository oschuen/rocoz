package rocsim.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class TileSelectionMoveButton extends PaintedImageButton {

  private static final long serialVersionUID = 1L;

  private boolean checked = false;

  public TileSelectionMoveButton() {
    super();
    setChecked(false);
  }

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    BasicStroke thickStroke = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    float dash1[] = { 10.0f };
    BasicStroke dashed = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, dash1, 0.0f);

    gr.setColor(Color.GRAY);
    gr.setStroke(thickStroke);
    gr.fillRect(5, 10, 40, 35);
    gr.setColor(Color.BLACK);
    gr.setStroke(dashed);
    gr.drawRect(15, 15, 40, 35);
    return image;
  }

  public void setChecked(boolean selected) {
    this.checked = selected;
    if (selected) {
      setBackground(Color.GREEN);
    } else {
      setBackground(Color.WHITE);
    }
  }

  /**
   * @return the checked
   */
  public boolean isChecked() {
    return this.checked;
  }
}
