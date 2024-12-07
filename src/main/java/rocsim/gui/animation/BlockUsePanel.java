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

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.BlockEventPanel;
import rocsim.schedule.model.TimeModel;

public class BlockUsePanel extends JScrollPane {
  private static final long serialVersionUID = 1L;
  private TimeModel timeModel;
//  private BlockStatusModel blockStatusModel;
  private List<BlockEventPanel> blockEventPanels = new ArrayList<>();

  public BlockUsePanel(BlockStatusModel blockStatusModel, StringListDataModel blockIdsModel, TimeModel timeModel) {
    this.timeModel = timeModel;
//    this.blockStatusModel = blockStatusModel;

    JPanel panel = new JPanel();
    setViewportView(panel);
    panel.setLayout(new GridLayout(0, 10, 0, 0));

    for (int i = 0; i < blockIdsModel.getSize(); i++) {
      String blockId = blockIdsModel.getElementAt(i);
      BlockEventPanel blockEventPanel = new BlockEventPanel(blockId, blockStatusModel, blockIdsModel, this.timeModel);
      this.blockEventPanels.add(blockEventPanel);
      panel.add(blockEventPanel);
    }
  }

  public void triggerRebuild() {
    for (BlockEventPanel blockEventPanel : this.blockEventPanels) {
      blockEventPanel.triggerRebuild();
    }
  }
}
