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
import rocsim.schedule.model.ScheduleModel;

public class ScheduleFrame extends ListFrame<SchedulePanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class SchedulePanelFactory
      implements rocsim.gui.widgets.ListFrame.ListItemFactory<SchedulePanel> {
    private EditorContext context;

    public SchedulePanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public SchedulePanel createNewItem() {
      return new SchedulePanel(this.context);
    }
  };

  public ScheduleFrame(EditorContext context) {
    super(new SchedulePanelFactory(context));
    this.context = context;

  }

  public void setScheduleModels(List<ScheduleModel> models) {
    List<SchedulePanel> panels = new ArrayList<>();
    for (ScheduleModel scheduleModel : models) {
      SchedulePanel panel = new SchedulePanel(this.context);
      panel.setModel(scheduleModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<ScheduleModel> getScheduleModels() {
    List<ScheduleModel> models = new ArrayList<>();
    for (SchedulePanel schedule : getContent()) {
      models.add(schedule.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (SchedulePanel schedules : getContent()) {
      schedules.updateContext();
    }
  }

  public void updatePreviousBlockIds(String blockId) {
    String blockIdK1 = blockId;
    for (SchedulePanel schedule : getContent()) {
      schedule.setPreviousBlockId(blockIdK1);
      blockIdK1 = schedule.getBlockId();
    }
  }
}
