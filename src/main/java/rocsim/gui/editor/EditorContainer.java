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

import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rocsim.gui.model.StringListDataModel;
import rocsim.schedule.model.LocoModel;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TrackPlanModel;
import rocsim.schedule.model.TripModel;

public class EditorContainer {

  private LocoFrame locoFrame;
  private TripFrame tripFrame;
  private TrackEditorFrame editorPanel;
  private StringListDataModel blockIdDataModel = new StringListDataModel();
  private StringListDataModel locoIdDataModel = new StringListDataModel();

  public EditorContainer(TimeModel timeModel) {

    this.locoFrame = new LocoFrame();
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

    this.editorPanel = new TrackEditorFrame(this.blockIdDataModel);
  }

  public JsonObject toJson() {
    JsonObjectBuilder builder = Json.createObjectBuilder();
    JsonArrayBuilder locoArr = Json.createArrayBuilder();
    List<LocoModel> locos = this.locoFrame.getLocoModels();
    for (LocoModel locoModel : locos) {
      locoArr.add(locoModel.toJson());
    }
    builder.add("locos", locoArr);
    JsonArrayBuilder tripArr = Json.createArrayBuilder();
    for (TripModel tripModel : this.tripFrame.getTripModels()) {
      tripArr.add(tripModel.toJson());
    }
    builder.add("trips", tripArr);

    builder.add("track", this.editorPanel.getTrackModel().toJson());
    return builder.build();
  }

  public void fromJson(JsonObject obj) {
    JsonArray locoArr = obj.getJsonArray("locos");
    List<LocoModel> locos = new ArrayList<>();
    for (int i = 0; i < locoArr.size(); i++) {
      LocoModel model = new LocoModel();
      model.fromJson(locoArr.getJsonObject(i));
      locos.add(model);
    }
    this.locoFrame.setLocoModels(locos);
    this.locoIdDataModel.setValueList(this.locoFrame.getLocoIds());

    TrackPlanModel trackModel = new TrackPlanModel();
    trackModel.fromJson(obj.getJsonArray("track"));
    this.editorPanel.setTrackModel(trackModel);
    this.blockIdDataModel.setValueList(trackModel.getBlockIds());

    JsonArray tripsArr = obj.getJsonArray("trips");
    List<TripModel> trips = new ArrayList<>();
    for (int i = 0; i < tripsArr.size(); i++) {
      TripModel model = new TripModel();
      model.fromJson(tripsArr.getJsonObject(i));
      trips.add(model);
    }
    this.tripFrame.setTripModels(trips);

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
