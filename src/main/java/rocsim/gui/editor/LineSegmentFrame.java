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
import rocsim.schedule.model.LineSegmentModel;

public class LineSegmentFrame extends ListFrame<LineSegmentPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class LineSegmentPanelFactory
      implements rocsim.gui.widgets.ListFrame.ListItemFactory<LineSegmentPanel> {
    private EditorContext context;

    public LineSegmentPanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public LineSegmentPanel createNewItem() {
      return new LineSegmentPanel(this.context);
    }
  };

  public LineSegmentFrame(EditorContext context) {
    super(new LineSegmentPanelFactory(context));
    this.context = context;
  }

  public void setLineSegmentModels(List<LineSegmentModel> models) {
    List<LineSegmentPanel> panels = new ArrayList<>();
    for (LineSegmentModel segmentModel : models) {
      LineSegmentPanel panel = new LineSegmentPanel(this.context);
      panel.setModel(segmentModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<LineSegmentModel> getLineSegmentModels() {
    List<LineSegmentModel> models = new ArrayList<>();
    for (LineSegmentPanel segement : getContent()) {
      models.add(segement.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (LineSegmentPanel segment : getContent()) {
      segment.updateContext();
    }

  }
}
