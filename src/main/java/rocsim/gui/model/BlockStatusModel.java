package rocsim.gui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rocsim.gui.tiles.Block;
import rocsim.gui.tiles.Block.BlockStatusListener;
import rocsim.gui.tiles.Tile.UseState;
import rocsim.schedule.model.TimeModel;

public class BlockStatusModel implements BlockStatusListener {

  private TimeModel timeModel;
  private Map<String, List<BlockEvent>> blockEvents = new HashMap<>();
  private int lastEventTime;
  private final List<BlockEvent> emptyList = new ArrayList<>();

  public static class BlockEvent {
    public int realTime = 0;
    public String blockId = "";
    public UseState useState = UseState.FREE;
  }

  public BlockStatusModel(StringListDataModel blockIdDataModel, TimeModel timeModel) {
    this.timeModel = timeModel;
    for (int i = 0; i < blockIdDataModel.getSize(); i++) {
      this.blockEvents.put(blockIdDataModel.getElementAt(i), new ArrayList<>());
    }
  }

  @Override
  public void statusChanged(Block block) {
    int currentTime = this.timeModel.getCurrentTime();
    if (this.lastEventTime < currentTime) {
      this.blockEvents.forEach((id, list) -> {
        list.removeIf((event) -> event.realTime < currentTime);
      });
    }
    if (block != null) {
      List<BlockEvent> eventList = this.blockEvents.get(block.getId());
      if (eventList != null) {
        BlockEvent event = new BlockEvent();
        event.realTime = this.timeModel.getCurrentTime();
        event.blockId = block.getId();
        event.useState = block.getState();
      }
    }
    this.lastEventTime = currentTime;
  }

  public List<BlockEvent> getEventsForId(String id) {
    return this.blockEvents.getOrDefault(id, this.emptyList);
  }
}
