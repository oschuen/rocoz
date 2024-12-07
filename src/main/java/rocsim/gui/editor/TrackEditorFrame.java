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
package rocsim.gui.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.TileSelectionPanel;
import rocsim.schedule.model.TrackPlanModel;

public class TrackEditorFrame extends JPanel {
  private static final long serialVersionUID = 1L;
  private TrackEditorPanel planPannel;
  private TileSelectionPanel panel;

  public TrackEditorFrame(StringListDataModel blockIdDataModel) {
    setLayout(new BorderLayout(0, 0));

    this.panel = new TileSelectionPanel();
    this.planPannel = new TrackEditorPanel(this.panel, blockIdDataModel);
    add(this.panel, BorderLayout.NORTH);
    add(this.planPannel, BorderLayout.CENTER);

  }

  public void setTrackModel(TrackPlanModel trackModel) {
    this.planPannel.setTrackModel(trackModel);

  }

}
