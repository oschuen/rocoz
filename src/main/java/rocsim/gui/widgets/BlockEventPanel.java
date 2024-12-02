package rocsim.gui.widgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.PriorityQueue;

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
  private final int resolution = 5;

  public BlockEventPanel(String blockId, BlockStatusModel blockStatusModel, TimeModel timeModel) {
    super();
    this.blockId = blockId;
    this.blockStatusModel = blockStatusModel;
    this.timeModel = timeModel;
  }

  /**
   * @param blockId the blockId to set
   */
  public void setBlockId(String blockId) {
    this.blockId = blockId;
  }

  @Override
  public Dimension getMinimumSize() {
    int maxTime = 0;
    PriorityQueue<BlockEvent> queue = this.blockStatusModel.getEventsForId(this.blockId);
    for (BlockEvent blockEvent : queue) {
      maxTime = Math.max(maxTime, blockEvent.realTime);
    }

    maxTime = (Math.max(maxTime, this.timeModel.getCurrentTime()) - this.timeModel.getBase()) / this.resolution;
    return new Dimension(50, maxTime);
  }

  @Override
  public void paint(Graphics gr) {
    super.paint(gr);
    int lastY = 0;
    UseState lastState = UseState.FREE;
    PriorityQueue<BlockEvent> queue = this.blockStatusModel.getEventsForId(this.blockId);
    for (BlockEvent blockEvent : queue) {
      int y = (blockEvent.realTime - this.timeModel.getBase()) / this.resolution;
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
    int y = (this.timeModel.getCurrentTime() - this.timeModel.getBase()) / this.resolution;
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
