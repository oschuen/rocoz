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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.BlockStatusModel.BlockEvent;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.TimeModel;

public class BlockEventPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private TimeModel timeModel;
  private BlockStatusModel blockStatusModel;
  private String blockId;
  private final int resolution = 4;
  private InnerPanel innerpanel;
  private JComboBox<String> comboBox;

  public BlockEventPanel(String blockId, BlockStatusModel blockStatusModel, List<String> blockIds,
      TimeModel timeModel) {
    super();
    this.blockId = blockId;
    this.blockStatusModel = blockStatusModel;
    this.timeModel = timeModel;

    setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    add(panel, BorderLayout.NORTH);

    this.comboBox = new JComboBox<>(blockIds.toArray(new String[blockIds.size()]));
    panel.add(this.comboBox);
    this.comboBox.setSelectedItem(blockId);
    this.comboBox.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
          BlockEventPanel.this.blockId = (String) BlockEventPanel.this.comboBox.getSelectedItem();
          javax.swing.SwingUtilities.invokeLater(() -> BlockEventPanel.this.repaint());
        }
      }
    });

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
      BlockEventPanel.this.innerpanel.repaint();
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
      int time = BlockEventPanel.this.timeModel.getMinTime() + event.getY() * BlockEventPanel.this.resolution;
      return BlockEventPanel.this.timeModel.getTimeSecString(time);
    }

    @Override
    public Dimension getPreferredSize() {
      return new Dimension(50,
          (BlockEventPanel.this.timeModel.getMaxTime() - BlockEventPanel.this.timeModel.getMinTime())
              / BlockEventPanel.this.resolution);
    }

    @Override
    public void paint(Graphics gr) {
      super.paint(gr);
      int lastY = 0;
      UseState lastState = UseState.FREE;
      PriorityQueue<BlockEvent> queue = BlockEventPanel.this.blockStatusModel
          .getEventsForId(BlockEventPanel.this.blockId);
      for (BlockEvent blockEvent : queue) {
        int y = (blockEvent.realTime - BlockEventPanel.this.timeModel.getMinTime()) / BlockEventPanel.this.resolution;
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
      int y = (BlockEventPanel.this.timeModel.getCurrentTime() - BlockEventPanel.this.timeModel.getMinTime())
          / BlockEventPanel.this.resolution;
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
