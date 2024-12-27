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
package rocsim.gui.widgets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JPanel;

public class ClockPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private int time;
  private boolean secShow;

  public ClockPanel(int time, boolean secShow) {
    super();
    this.time = time;
    this.secShow = secShow;
    setPreferredSize(new Dimension(100, 100));
  }

  @Override
  public void paint(Graphics gr) {
    super.paint(gr);
    float r = Math.min(getWidth(), getHeight()) / 2.0F;
    gr.setColor(Color.WHITE);
    gr.fillOval(0, 0, (int) (2 * r - 1), (int) (2 * r - 1));
    gr.setColor(Color.BLACK);

    Graphics2D g2 = (Graphics2D) gr;
    g2.setStroke(new BasicStroke(3));
    double w;
    g2.setStroke(new BasicStroke(3));

    for (int i = 0; i < 360; i = i + 30) {
      w = Math.toRadians(i);
      g2.draw(new Line2D.Double(r + (r - 2) * Math.sin(w), r - (r - 2) * Math.cos(w), r + (r - 8) * Math.sin(w),
          r - (r - 8) * Math.cos(w)));
    }
    g2.setStroke(new BasicStroke(3));
    int sec = this.time % 60;
    int min = (this.time / 60) % 60;
    int hour = this.time / 3600;
    String time = String.format("%02d:%02d", hour, min);
    g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
    FontMetrics metrics = g2.getFontMetrics(g2.getFont());
    int adv = metrics.stringWidth(time);
    g2.drawString(time, r - adv / 2, 6 * r / 4);
    w = Math.toRadians(6 * min);

    g2.draw(new Line2D.Double(r, r, r + (r - 15) * Math.sin(w), r - (r - 15) * Math.cos(w)));

    w = Math.toRadians(30 * hour);

    g2.draw(new Line2D.Double(r, r, r + (r * 0.5) * Math.sin(w), r - (r * 0.5) * Math.cos(w)));

    if (this.secShow) {
      w = Math.toRadians(6 * sec);

      gr.setColor(Color.RED);
      g2.draw(new Line2D.Double(r, r, r + (r - 15) * Math.sin(w), r - (r - 15) * Math.cos(w)));

    }

  }

  /**
   * @param time the time to set
   */
  public void setTime(int time) {
    this.time = time;
    java.awt.EventQueue.invokeLater(() -> {
      this.repaint();
    });
  }
}
