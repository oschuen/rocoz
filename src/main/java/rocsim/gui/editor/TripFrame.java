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

import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;
import rocsim.schedule.model.TripModel;

public class TripFrame extends ListFrame<TripPanel> {
  private static final long serialVersionUID = 1L;
  private StringListDataModel blockIdDataModel;
  private StringListDataModel locoIdDataModel;
  private TimeModel timeModel;
  private TimeModelChangeListener timeChangeListener = new TimeModelChangeListener() {

    @Override
    public void timeModelChanged() {
      for (TripPanel tripPanel : TripFrame.this.getContent()) {
        tripPanel.adjustTime();
      }
    }
  };

  private static class TripPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<TripPanel> {
    private TimeModel timeModel;
    private StringListDataModel blockIdDataModel;
    private StringListDataModel locoIdDataModel;

    TripPanelFactory(TimeModel timeModel, StringListDataModel locoIdDataModel, StringListDataModel blockIdDataModel) {
      this.timeModel = timeModel;
      this.locoIdDataModel = locoIdDataModel;
      this.blockIdDataModel = blockIdDataModel;
    }

    @Override
    public TripPanel createNewItem() {
      return new TripPanel(this.timeModel, this.locoIdDataModel, this.blockIdDataModel);
    }
  };

  public TripFrame(TimeModel timeModel, StringListDataModel locoIdDataModel, StringListDataModel blockIdDataModel) {
    super(new TripPanelFactory(timeModel, locoIdDataModel, blockIdDataModel));
    this.blockIdDataModel = blockIdDataModel;
    this.locoIdDataModel = locoIdDataModel;
    this.timeModel = timeModel;
    this.timeModel.addListener(TripFrame.this.timeChangeListener);
  }

  public void setTripModels(List<TripModel> tripModels) {
    List<TripPanel> panels = new ArrayList<>();
    for (TripModel tripModel : tripModels) {
      TripPanel panel = new TripPanel(this.timeModel, this.locoIdDataModel, this.blockIdDataModel);
      panel.setModel(tripModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<TripModel> getTripModels() {
    List<TripModel> models = new ArrayList<>();
    for (TripPanel trip : getContent()) {
      models.add(trip.getModel());
    }
    return models;
  }
}
