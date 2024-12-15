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
import rocsim.schedule.model.LineModel;

public class LineFrame extends ListFrame<LinePanel> {

  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class LinePanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<LinePanel> {

    private EditorContext context;

    LinePanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public LinePanel createNewItem() {
      return new LinePanel(this.context);
    }
  };

  public LineFrame(EditorContext context) {
    super(new LinePanelFactory(context));
    this.context = context;
  }

  public void setLineModels(List<LineModel> lineModels) {
    List<LinePanel> panels = new ArrayList<>();
    for (LineModel lineModel : lineModels) {
      LinePanel panel = new LinePanel(this.context);
      panel.setModel(lineModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<LineModel> getLineModels() {
    List<LineModel> models = new ArrayList<>();
    for (LinePanel line : getContent()) {
      models.add(line.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (LinePanel line : getContent()) {
      line.updateContext();
    }
  }
}
