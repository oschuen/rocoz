/*
 * Copyright © 2023 Oliver Schünemann (oschuen@users.noreply.github.com)
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
package rocsim.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class TileDeleteButton extends PaintedImageButton {
  private static final long serialVersionUID = 1L;
  private boolean checked = false;

  public TileDeleteButton() {
    super();
    setChecked(false);
  }

  @Override
  protected Image createImage() {
    BufferedImage image = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
    Graphics2D gr = (Graphics2D) image.getGraphics();
    BasicStroke thickStroke = new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    gr.setColor(Color.GRAY);
    gr.fillRect(5, 27, 50, 6);
    gr.setStroke(thickStroke);
    gr.setColor(Color.RED);
    gr.drawLine(10, 10, 50, 50);
    gr.drawLine(10, 50, 50, 10);
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
