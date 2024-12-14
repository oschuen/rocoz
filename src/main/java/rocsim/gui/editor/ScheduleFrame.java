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
import rocsim.schedule.model.ScheduleStationModel;

public class ScheduleFrame extends ListFrame<ScheduleStationPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class SchedulePanelFactory
      implements rocsim.gui.widgets.ListFrame.ListItemFactory<ScheduleStationPanel> {
    private EditorContext context;

    public SchedulePanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public ScheduleStationPanel createNewItem() {
      return new ScheduleStationPanel(this.context);
    }
  };

  public ScheduleFrame(EditorContext context) {
    super(new SchedulePanelFactory(context));
    this.context = context;

  }

  public void setScheduleModels(List<ScheduleStationModel> models) {
    List<ScheduleStationPanel> panels = new ArrayList<>();
    for (ScheduleStationModel scheduleModel : models) {
      ScheduleStationPanel panel = new ScheduleStationPanel(this.context);
      panel.setModel(scheduleModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<ScheduleStationModel> getScheduleModels() {
    List<ScheduleStationModel> models = new ArrayList<>();
    for (ScheduleStationPanel schedule : getContent()) {
      models.add(schedule.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (ScheduleStationPanel schedules : getContent()) {
      schedules.updateContext();
    }
  }
}
