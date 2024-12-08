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

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

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
    blockIdDataModel.addListDataListener(new ListDataListener() {

      @Override
      public void intervalRemoved(ListDataEvent arg0) {
        // TODO Auto-generated method stub
        contentsChanged(arg0);
      }

      @Override
      public void intervalAdded(ListDataEvent arg0) {
        contentsChanged(arg0);
      }

      @Override
      public void contentsChanged(ListDataEvent arg0) {
        Map<String, PriorityQueue<BlockEvent>> oldEvents = BlockStatusModel.this.blockEvents;
        BlockStatusModel.this.blockEvents = new HashMap<>();
        for (int i = 0; i < blockIdDataModel.getSize(); i++) {
          String blockId = blockIdDataModel.getElementAt(i);
          PriorityQueue<BlockEvent> oldQueue = oldEvents.get(blockId);
          if (oldQueue == null) {
            BlockStatusModel.this.blockEvents.put(blockId,
                new PriorityQueue<>((a, b) -> Integer.compare(a.realTime, b.realTime)));
          } else {
            BlockStatusModel.this.blockEvents.put(blockId, oldQueue);
          }
        }
      }
    });
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
