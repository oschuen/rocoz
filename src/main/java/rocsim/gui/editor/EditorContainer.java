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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rocsim.gui.model.StringListDataModel;
import rocsim.schedule.model.LineModel;
import rocsim.schedule.model.LocoModel;
import rocsim.schedule.model.StationModel;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TrackPlanModel;
import rocsim.schedule.model.TripModel;

public class EditorContainer {

  private JScrollPane locoFrameWrapper;
  private LocoFrame locoFrame;
  private JScrollPane tripFrameWrapper;
  private TripFrame tripFrame;
  private TrackEditorFrame editorPanel;
  private JScrollPane stationFrameWrapper;
  private StationFrame stationFrame;
  private StringListDataModel blockIdDataModel = new StringListDataModel();
  private StringListDataModel locoIdDataModel = new StringListDataModel();

  private JScrollPane lineFrameWrapper;
  private LineFrame lineFrame;

  private EditorContext myContext = new EditorContext() {

    @Override
    public List<String> getArrivableBlockIds() {
      TrackPlanModel trackModel = EditorContainer.this.editorPanel.getTrackModel();
      return trackModel.getArrivableBlockIds();
    }

    @Override
    public List<String> getWatchBlockIds() {
      TrackPlanModel trackModel = EditorContainer.this.editorPanel.getTrackModel();
      return trackModel.getWatchBlockIds();
    }

    @Override
    public List<String> getStationNames() {
      List<StationModel> stationModels = EditorContainer.this.stationFrame.getStationModels();
      return stationModels.stream().map(stationModel -> {
        return stationModel.getName();
      }).collect(Collectors.toList());
    }
  };

  public EditorContainer(TimeModel timeModel) {

    this.locoFrame = new LocoFrame();
    this.locoFrameWrapper = new JScrollPane(this.locoFrame);

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
    this.tripFrameWrapper = new JScrollPane(this.tripFrame);

    this.editorPanel = new TrackEditorFrame(this.blockIdDataModel);

    this.stationFrame = new StationFrame(this.myContext);
    this.stationFrameWrapper = new JScrollPane(this.stationFrame);

    this.lineFrame = new LineFrame(this.myContext);
    this.lineFrameWrapper = new JScrollPane(this.lineFrame);
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

    JsonArrayBuilder stationArr = Json.createArrayBuilder();
    for (StationModel stationModel : this.stationFrame.getStationModels()) {
      stationArr.add(stationModel.toJson());
    }
    builder.add("stations", stationArr);

    JsonArrayBuilder lineArr = Json.createArrayBuilder();
    for (LineModel lineModel : this.lineFrame.getLineModels()) {
      lineArr.add(lineModel.toJson());
    }
    builder.add("lines", lineArr);
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
    this.blockIdDataModel.setValueList(trackModel.getArrivableBlockIds());

    JsonArray tripsArr = obj.getJsonArray("trips");
    List<TripModel> trips = new ArrayList<>();
    for (int i = 0; i < tripsArr.size(); i++) {
      TripModel model = new TripModel();
      model.fromJson(tripsArr.getJsonObject(i));
      trips.add(model);
    }
    this.tripFrame.setTripModels(trips);

    JsonArray stationArr = obj.getJsonArray("stations");
    List<StationModel> stations = new ArrayList<>();
    for (int i = 0; stationArr != null && i < stationArr.size(); i++) {
      StationModel model = new StationModel();
      model.fromJson(stationArr.getJsonObject(i));
      stations.add(model);
    }
    this.stationFrame.setStationModels(stations);

    JsonArray lineArr = obj.getJsonArray("lines");
    List<LineModel> lines = new ArrayList<>();
    for (int i = 0; lineArr != null && i < lineArr.size(); i++) {
      LineModel model = new LineModel();
      model.fromJson(lineArr.getJsonObject(i));
      lines.add(model);
    }
    this.lineFrame.setLineModels(lines);
  }

  /**
   * @return the locoFrame
   */
  public JScrollPane getLocoFrame() {
    return this.locoFrameWrapper;
  }

  /**
   * @return the tripFrame
   */
  public JScrollPane getTripFrame() {
    return this.tripFrameWrapper;
  }

  /**
   * @return the editorPanel
   */
  public TrackEditorFrame getEditorPanel() {
    return this.editorPanel;
  }

  /**
   * @return the stationFrame
   */
  public JScrollPane getStationFrame() {
    return this.stationFrameWrapper;
  }

  public void frameSelected(Component selectedComponent) {
    if (selectedComponent.equals(this.stationFrameWrapper)) {
      this.stationFrame.updateContext();
    } else if (selectedComponent.equals(this.lineFrameWrapper)) {
      this.lineFrame.updateContext();
    }
  }

  /**
   * @return the lineFrameWrapper
   */
  public JScrollPane getLineFrame() {
    return this.lineFrameWrapper;
  }
}
