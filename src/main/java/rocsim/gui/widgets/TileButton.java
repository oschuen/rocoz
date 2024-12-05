package rocsim.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import rocsim.gui.tiles.Tile;

public class TileButton extends JButton {

  private static final long serialVersionUID = 1L;
  private TileFactory factory;
  private boolean selected = false;

  public interface TileFactory {

    Tile createTile();
  }

  public TileButton(TileFactory factory) {
    this.factory = factory;
    final int dim = 60;
    BufferedImage image = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
    Tile tile = factory.createTile();
    tile.setX(0);
    tile.setY(0);
    tile.draw(dim, (Graphics2D) image.getGraphics());
    setIcon(new ImageIcon(image));
    setPreferredSize(new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));
    setBackground(Color.WHITE);
  }

  /**
   * @return the selected
   */
  @Override
  public boolean isSelected() {
    return this.selected;
  }

  public Tile createTile() {
    return this.factory.createTile();
  }

  /**
   * @param selected the selected to set
   */
  @Override
  public void setSelected(boolean selected) {
    this.selected = selected;
    if (selected) {
      setBackground(Color.GREEN);
    } else {
      setBackground(Color.WHITE);
    }
  }

}
