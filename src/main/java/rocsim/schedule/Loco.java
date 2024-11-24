package rocsim.schedule;

import java.awt.Point;

public class Loco {
  private Point location = new Point(-1, -1);
  private String id = "";
  private int vMax = 80;
  private boolean inBw = true;

  public Loco(String id, int vMax) {
    super();
    this.id = id;
    this.vMax = vMax;
  }

  /**
   * @return the location
   */
  public Point getLocation() {
    return this.location;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(Point location) {
    this.location = location;
  }

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @return the vMax
   */
  public int getvMax() {
    return this.vMax;
  }

  /**
   * @return the inBw
   */
  public boolean isInBw() {
    return this.inBw;
  }

  /**
   * @param inBw the inBw to set
   */
  public void setInBw(boolean inBw) {
    this.inBw = inBw;
  }

}
