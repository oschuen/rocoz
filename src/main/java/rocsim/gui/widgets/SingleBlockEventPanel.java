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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.PriorityQueue;

import javax.swing.JLabel;
import javax.swing.JPanel;

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.BlockStatusModel.BlockEvent;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.TimeModel;

public class SingleBlockEventPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private TimeModel timeModel;
  private String blockId;
  private final int resolution = 4;
  private InnerPanel innerpanel;
  private JLabel blockIdLabel;
  private BlockStatusModel blockStatusModel;

  public SingleBlockEventPanel(String blockId, BlockStatusModel blockStatusModel, TimeModel timeModel) {
    super();
    this.blockId = blockId;
    this.timeModel = timeModel;
    this.blockStatusModel = blockStatusModel;

    setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    add(panel, BorderLayout.NORTH);

    this.blockIdLabel = new JLabel(blockId);
    panel.add(this.blockIdLabel);

    this.innerpanel = new InnerPanel();
    add(this.innerpanel, BorderLayout.CENTER);
  }

  /**
   * @param blockId the blockId to set
   */
  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  /**
   * @return the blockId
   */
  public String getBlockId() {
    return this.blockId;
  }

  public void triggerRebuild() {
    javax.swing.SwingUtilities.invokeLater(() -> {
      SingleBlockEventPanel.this.innerpanel.repaint();
    });
  }

  private class InnerPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public InnerPanel() {
      super();
      setToolTipText("");
    }

    @Override
    public String getToolTipText(MouseEvent event) {
      int time = SingleBlockEventPanel.this.timeModel.getMinTime()
          + event.getY() * SingleBlockEventPanel.this.resolution;
      return SingleBlockEventPanel.this.timeModel.getTimeSecString(time);
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(50,
          (SingleBlockEventPanel.this.timeModel.getMaxTime() - SingleBlockEventPanel.this.timeModel.getMinTime())
              / SingleBlockEventPanel.this.resolution);
    }

    @Override
    public void paint(Graphics gr) {
      super.paint(gr);
      int lastY = 0;
      UseState lastState = UseState.FREE;
      PriorityQueue<BlockEvent> queue = SingleBlockEventPanel.this.blockStatusModel
          .getEventsForId(SingleBlockEventPanel.this.blockId);
      for (BlockEvent blockEvent : queue) {
        int y = (blockEvent.realTime - SingleBlockEventPanel.this.timeModel.getMinTime())
            / SingleBlockEventPanel.this.resolution;
        switch (lastState) {
        case BLOCK:
          gr.setColor(Color.YELLOW);
          break;
        case TRAIN:
          gr.setColor(Color.RED);
          break;
        case FREE:
        default:
          gr.setColor(Color.GRAY);
          break;
        }
        gr.fillRect(0, lastY, getWidth(), y - lastY);
        lastY = y;
        lastState = blockEvent.useState;
      }
      int y = (SingleBlockEventPanel.this.timeModel.getCurrentTime()
          - SingleBlockEventPanel.this.timeModel.getMinTime()) / SingleBlockEventPanel.this.resolution;
      if (y > lastY) {
        switch (lastState) {
        case BLOCK:
          gr.setColor(Color.YELLOW);
          break;
        case TRAIN:
          gr.setColor(Color.RED);
          break;
        case FREE:
        default:
          gr.setColor(Color.GRAY);
          break;
        }
        gr.fillRect(0, lastY, getWidth(), y - lastY);
      }
    }
  }
}
