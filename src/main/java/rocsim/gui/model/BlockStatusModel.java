package rocsim.gui.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import rocsim.gui.tiles.Block;
import rocsim.gui.tiles.Block.BlockStatusListener;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.TimeModel;

public class BlockStatusModel implements BlockStatusListener {

  private TimeModel timeModel;
  private Map<String, PriorityQueue<BlockEvent>> blockEvents = new HashMap<>();
  private int lastEventTime;
  private final PriorityQueue<BlockEvent> emptyList = new PriorityQueue<>();

  public static class BlockEvent {
    public int realTime = 0;
    public String blockId = "";
    public UseState useState = UseState.FREE;
  }

  public BlockStatusModel(StringListDataModel blockIdDataModel, TimeModel timeModel) {
    this.timeModel = timeModel;
    for (int i = 0; i < blockIdDataModel.getSize(); i++) {
      this.blockEvents.put(blockIdDataModel.getElementAt(i),
          new PriorityQueue<>((a, b) -> Integer.compare(a.realTime, b.realTime)));
    }
  }

  @Override
  public void statusChanged(Block block) {
    int currentTime = this.timeModel.getCurrentTime();
    if (this.lastEventTime > currentTime) {
      this.blockEvents.forEach((id, list) -> {
        list.removeIf((event) -> event.realTime > currentTime);
      });
    }
    if (block != null) {
      PriorityQueue<BlockEvent> eventList = this.blockEvents.get(block.getId());
      if (eventList != null) {
        BlockEvent event = new BlockEvent();
        event.realTime = this.timeModel.getCurrentTime();
        event.blockId = block.getId();
        event.useState = block.getState();
        eventList.add(event);
      }
    }
    this.lastEventTime = currentTime;
  }

  public PriorityQueue<BlockEvent> getEventsForId(String id) {
    return this.blockEvents.getOrDefault(id, this.emptyList);
  }
}
