package rocsim.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

public class RightSwitch extends Tile {

  public RightSwitch(int x, int y, Direction orientation) {
    super(x, y, orientation);
  }

  protected void innerDrawSwitch(int raster, Graphics2D g) {
    g.setColor(Color.black);
    int x[] = new int[] { 3 * raster / 4, raster / 4, 0, 0 };
    int y[] = new int[] { 0, 0, raster / 4, 3 * raster / 4 };
    g.fillPolygon(x, y, 4);
    if (this.switched) {
      switch (getState()) {
      case BLOCK:
        g.setColor(Color.YELLOW);
        break;
      case FREE:
        g.setColor(Color.LIGHT_GRAY);
        break;
      case TRAIN:
        g.setColor(Color.RED);
        break;
      default:
        g.setColor(Color.GREEN);
        break;
      }
    } else {
      g.setColor(Color.DARK_GRAY);
    }
    g.translate(raster / 4, raster / 4);
    g.rotate(Math.toRadians(-45));
    g.fillRect(-raster / 6, -raster / 16, raster / 3, raster / 8);
  }

  protected void innerDrawStraight(int raster, Graphics2D g) {

    g.setColor(Color.BLACK);
    g.fillRect(raster / 4, 0, raster / 2, raster);
    if (this.switched) {
      g.setColor(Color.DARK_GRAY);
    } else {
      switch (getState()) {
      case BLOCK:
        g.setColor(Color.YELLOW);
        break;
      case FREE:
        g.setColor(Color.LIGHT_GRAY);
        break;
      case TRAIN:
        g.setColor(Color.RED);
        break;
      default:
        g.setColor(Color.GREEN);
        break;
      }

    }
    g.fillRect(3 * raster / 8, raster / 4, raster / 4, raster / 2);
  }

  @Override
  protected void innerDraw(int raster, Graphics2D g) {
    if (this.switched) {
      innerDrawStraight(raster, (Graphics2D) g.create());
      innerDrawSwitch(raster, (Graphics2D) g.create());
    } else {
      innerDrawSwitch(raster, (Graphics2D) g.create());
      innerDrawStraight(raster, (Graphics2D) g.create());
    }

  }

  @Override
  public Direction[] getPossibleDirections() {
    return new Direction[] { Direction.values()[getOrientation().ordinal()],
        Direction.values()[(getOrientation().ordinal() + 2) % 4],
        Direction.values()[(getOrientation().ordinal() + 1) % 4] };
  }

  @Override
  public Optional<Direction> getFollowUpDirection(Direction dir) {
    if (this.switched) {
      if (getOrientation().ordinal() == dir.ordinal()) {
        return Optional.of(Direction.values()[(dir.ordinal() + 1) % 4]);
      } else if (getOrientation().ordinal() == (dir.ordinal() + 1) % 4) {
        return Optional.of(Direction.values()[(dir.ordinal() + 3) % 4]);
      }
    } else {
      if (getOrientation().ordinal() % 2 == dir.ordinal() % 2) {
        return Optional.of(dir);
      }
    }
    return Optional.empty();

  }

  /**
   * @return the switched
   */
  public boolean isSwitched() {
    return this.switched;
  }

  /**
   * @param switched the switched to set
   */
  public void setSwitched(boolean switched) {
    this.switched = switched;
  }

  private boolean switched = false;

  public static void main(String[] args) {
    BufferedImage br = new BufferedImage(400, 400, BufferedImage.TYPE_INT_RGB);
    RightSwitch weiche = new RightSwitch(3, 4, Direction.NORTH);
    Graphics2D gr = br.createGraphics();
    gr.setColor(Color.WHITE);
    gr.fillRect(1, 1, 398, 398);
    weiche.setSwitched(true);

    gr.setColor(Color.BLACK);
    gr.drawRect(0, 0, 40, 40);

    weiche.setX(1);
    weiche.setY(1);
    weiche.setOrientation(Direction.NORTH);
    weiche.setState(UseState.BLOCK);
    weiche.draw(40, gr);
    System.out.println(weiche.getFollowUpDirection(Direction.NORTH));
    System.out.println(weiche.getFollowUpDirection(Direction.WEST));
    System.out.println(weiche.getFollowUpDirection(Direction.SOUTH));
    System.out.println(weiche.getFollowUpDirection(Direction.EAST));

    weiche.setX(3);
    weiche.setY(1);
    weiche.setOrientation(Direction.WEST);
    weiche.setState(UseState.TRAIN);
    weiche.draw(40, gr);

    weiche.setX(5);
    weiche.setY(1);
    weiche.setOrientation(Direction.SOUTH);
    weiche.setState(UseState.FREE);
    weiche.draw(40, gr);

    weiche.setX(7);
    weiche.setY(1);
    weiche.setOrientation(Direction.EAST);
    weiche.draw(40, gr);

    try {
      ImageIO.write(br, "PNG", new File("track.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
