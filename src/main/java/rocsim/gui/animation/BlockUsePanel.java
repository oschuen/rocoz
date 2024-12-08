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
package rocsim.gui.animation;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.BlockEventPanel;
import rocsim.schedule.model.TimeModel;

public class BlockUsePanel extends JScrollPane {
  private static final long serialVersionUID = 1L;
  private TimeModel timeModel;
  private StringListDataModel blockIdsModel;
  private BlockStatusModel blockStatusModel;
  private JPanel panel;
  private List<BlockEventPanel> blockEventPanels = new ArrayList<>();

  public BlockUsePanel(BlockStatusModel blockStatusModel, StringListDataModel blockIdsModel, TimeModel timeModel) {
    this.timeModel = timeModel;
    this.blockStatusModel = blockStatusModel;
    this.blockIdsModel = blockIdsModel;

    this.panel = new JPanel();
    setViewportView(this.panel);
    this.panel.setLayout(new GridLayout(0, 10, 0, 0));

    for (int i = 0; i < blockIdsModel.getSize(); i++) {
      String blockId = blockIdsModel.getElementAt(i);
      BlockEventPanel blockEventPanel = new BlockEventPanel(blockId, blockStatusModel,
          getBlockIdsFromModel(blockIdsModel), this.timeModel);
      this.blockEventPanels.add(blockEventPanel);
      this.panel.add(blockEventPanel);
    }
    blockIdsModel.addListDataListener(new ListDataListener() {

      @Override
      public void intervalRemoved(ListDataEvent arg0) {
        contentsChanged(arg0);
      }

      @Override
      public void intervalAdded(ListDataEvent arg0) {
        contentsChanged(arg0);
      }

      @Override
      public void contentsChanged(ListDataEvent arg0) {
        System.out.println("Reorganize");
        reorganize();
      }
    });
  }

  private List<String> getBlockIdsFromModel(StringListDataModel model) {
    List<String> blockIds = new ArrayList<>();
    for (int i = 0; i < model.getSize(); i++) {
      blockIds.add(model.getElementAt(i));
    }
    return blockIds;
  }

  private void reorganize() {

    List<String> blockIds = getBlockIdsFromModel(this.blockIdsModel);
    List<String> modelBlockIds = getBlockIdsFromModel(this.blockIdsModel);
    List<BlockEventPanel> newBlockPanel = new ArrayList<>();
    for (BlockEventPanel blockEventPanel : this.blockEventPanels) {
      if (blockIds.contains(blockEventPanel.getBlockId())) {
        newBlockPanel.add(
            new BlockEventPanel(blockEventPanel.getBlockId(), this.blockStatusModel, modelBlockIds, this.timeModel));
        blockIds.remove(blockEventPanel.getBlockId());
      }
    }
    for (String newBlockId : blockIds) {
      System.out.println("new Block panel" + newBlockId);
      newBlockPanel.add(new BlockEventPanel(newBlockId, this.blockStatusModel, modelBlockIds, this.timeModel));
    }
    this.panel.removeAll();
    for (BlockEventPanel blockEventPanel : newBlockPanel) {
      this.panel.add(blockEventPanel);
    }
    this.blockEventPanels = newBlockPanel;
    repaint();
    revalidate();
  }

  public void triggerRebuild() {
    for (BlockEventPanel blockEventPanel : this.blockEventPanels) {
      blockEventPanel.triggerRebuild();
    }
  }
}
