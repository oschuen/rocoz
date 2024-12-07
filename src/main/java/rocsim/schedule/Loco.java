/*
 * Copyright © 2024 Oliver Schünemann (oschuen@users.noreply.github.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rocsim.schedule;

import java.awt.Point;

import rocsim.schedule.model.LocoModel;

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

  public Loco(LocoModel model) {
    super();
    this.id = model.getId();
    this.vMax = model.getvMax();
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
