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
import rocsim.schedule.model.PlatformModel;

public class PlatformFrame extends ListFrame<PlatformPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class PlatformPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<PlatformPanel> {
    private EditorContext context;

    PlatformPanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public PlatformPanel createNewItem() {
      return new PlatformPanel(this.context);
    }
  };

  public PlatformFrame(EditorContext context) {
    super(new PlatformPanelFactory(context));
    this.context = context;

  }

  public void setPlatformModels(List<PlatformModel> models) {
    List<PlatformPanel> panels = new ArrayList<>();
    for (PlatformModel platformModel : models) {
      PlatformPanel panel = new PlatformPanel(this.context);
      panel.setModel(platformModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<PlatformModel> getPlatformModels() {
    List<PlatformModel> models = new ArrayList<>();
    for (PlatformPanel platform : getContent()) {
      models.add(platform.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (PlatformPanel platform : getContent()) {
      platform.updateContext();
    }
  }
}
