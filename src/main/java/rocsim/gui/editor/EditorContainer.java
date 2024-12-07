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

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rocsim.gui.model.StringListDataModel;
import rocsim.schedule.model.TimeModel;
import rocsim.xml.ReadPlan;

public class EditorContainer {

  private LocoFrame locoFrame;
  private TripFrame tripFrame;
  private TrackEditorFrame editorPanel;
  private StringListDataModel blockIdDataModel = new StringListDataModel();
  private StringListDataModel locoIdDataModel = new StringListDataModel();

  public EditorContainer(ReadPlan planner, TimeModel timeModel) {

    this.blockIdDataModel.setValueList(planner.getTrackModel().getBlockIds());
    //
    this.locoFrame = new LocoFrame();
    this.locoFrame.setLocoModels(planner.getLocoModels());
    this.locoIdDataModel.setValueList(this.locoFrame.getLocoIds());
    this.locoFrame.addDataListener(new ListDataListener() {

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
        List<String> ids = EditorContainer.this.locoFrame.getLocoIds();
        EditorContainer.this.locoIdDataModel.setValueList(ids);
      }
    });

    this.tripFrame = new TripFrame(timeModel, this.locoIdDataModel, this.blockIdDataModel);
    this.tripFrame.setTripModels(planner.getTripModels());

    this.editorPanel = new TrackEditorFrame(this.blockIdDataModel);
    this.editorPanel.setTrackModel(planner.getTrackModel());

  }

  /**
   * @return the locoFrame
   */
  public JScrollPane getLocoFrame() {
    return new JScrollPane(this.locoFrame);
  }

  /**
   * @return the tripFrame
   */
  public JScrollPane getTripFrame() {
    return new JScrollPane(this.tripFrame);
  }

  /**
   * @return the editorPanel
   */
  public TrackEditorFrame getEditorPanel() {
    return this.editorPanel;
  }
}
