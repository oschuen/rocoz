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

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.TripModel;

public class TripFrame extends ListFrame<TripStationPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class TripPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<TripStationPanel> {
    private EditorContext context;

    TripPanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public TripStationPanel createNewItem() {
      return new TripStationPanel(this.context);
    }
  };

  public TripFrame(EditorContext context) {
    super(new TripPanelFactory(context));
    this.context = context;
  }

  public void setTripModels(List<TripModel> tripModels) {
    List<TripStationPanel> panels = new ArrayList<>();
    for (TripModel tripModel : tripModels) {
      TripStationPanel panel = new TripStationPanel(this.context);
      panel.setModel(tripModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<TripModel> getTripModels() {
    List<TripModel> models = new ArrayList<>();
    for (TripStationPanel trip : getContent()) {
      models.add(trip.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (TripStationPanel trip : getContent()) {
      trip.updateContext();
    }
  }

  public void updateModel(String tripId, TripModel updatedModel) {
    for (TripStationPanel trip : getContent()) {
      TripModel tripModel = trip.getModel();
      if (tripModel.getId().equals(tripId)) {
        trip.setModel(updatedModel);
      }
    }
  }
}
