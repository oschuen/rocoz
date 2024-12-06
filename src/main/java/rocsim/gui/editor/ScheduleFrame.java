/*
 * Copyright © 2023 Oliver Schünemann (oschuen@users.noreply.github.com)
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
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel;

public class ScheduleFrame extends ListFrame<SchedulePanel> {
  private static final long serialVersionUID = 1L;
  private StringListDataModel blockIdDataModel;
  private TimeModel timeModel;

  private static class SchedulePanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<SchedulePanel> {
    private StringListDataModel blockIdDataModel;
    private TimeModel timeModel;

    public SchedulePanelFactory(TimeModel timeModel, StringListDataModel blockIdDataModel) {
      this.blockIdDataModel = blockIdDataModel;
    }

    @Override
    public SchedulePanel createNewItem() {
      return new SchedulePanel(this.timeModel, this.blockIdDataModel);
    }
  };

  public ScheduleFrame(TimeModel timeModel, StringListDataModel blockIdDataModel) {
    super(new SchedulePanelFactory(timeModel, blockIdDataModel));
    this.blockIdDataModel = blockIdDataModel;
    this.timeModel = timeModel;

  }

  public void setScheduleModels(List<ScheduleModel> models) {
    List<SchedulePanel> panels = new ArrayList<>();
    for (ScheduleModel scheduleModel : models) {
      SchedulePanel panel = new SchedulePanel(this.timeModel, this.blockIdDataModel);
      panel.setModel(scheduleModel);
      panels.add(panel);
    }
    setContent(panels);

  }

}
