package rocsim.gui.widgets;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public abstract class PaintedImageButton extends JButton {
  private static final long serialVersionUID = 1L;

  public PaintedImageButton() {
    super();
    super.setIcon(new ImageIcon(createImage()));
    super.setPreferredSize(new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));
  }

  protected abstract Image createImage();

}
