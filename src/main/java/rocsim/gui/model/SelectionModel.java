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
package rocsim.gui.model;

import java.awt.Point;
import java.awt.Rectangle;

public class SelectionModel {
  private Rectangle source = new Rectangle();
  private Point dest = new Point();
  private boolean isSelected = false;
  private boolean dragging = false;

  /**
   * @return the source
   */
  public Rectangle getSource() {
    return new Rectangle(this.source);
  }

  /**
   * @param source the source to set
   */
  public void setSource(Rectangle source) {
    this.source.x = source.x;
    this.source.y = source.y;
    this.source.width = source.width;
    this.source.height = source.height;
  }

  /**
   * @return the dest
   */
  public Point getDest() {
    return new Point(this.dest);
  }

  /**
   * @param dest the dest to set
   */
  public void setDest(Point dest) {
    this.dest.x = dest.x;
    this.dest.y = dest.y;
  }

  /**
   * @return the isSelected
   */
  public boolean isSelected() {
    return this.isSelected;
  }

  /**
   * @param isSelected the isSelected to set
   */
  public void setSelected(boolean isSelected) {
    this.isSelected = isSelected;
  }

  /**
   * @return the dragging
   */
  public boolean isDragging() {
    return this.dragging;
  }

  /**
   * @param dragging the dragging to set
   */
  public void setDragging(boolean dragging) {
    this.dragging = dragging;
  }

  public boolean isInDestSelection(Point p) {
    return p.x >= this.dest.x && p.x < this.dest.x + this.source.width && p.y >= this.dest.y
        && p.y < this.dest.y + this.source.height;
  }

  public boolean isInSourceSelection(Point p) {
    return p.x >= this.source.x && p.x < this.source.x + this.source.width && p.y >= this.source.y
        && p.y < this.source.y + this.source.height;
  }

}
