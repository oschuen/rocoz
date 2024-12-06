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

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.LocoModel;

public class LocoFrame extends ListFrame<LocoPanel> {

  private static final long serialVersionUID = 1L;

  public LocoFrame() {
    super(new LocoPanel.LocoPanelFactory());
  }

  public void setLocoModels(List<LocoModel> models) {
    List<LocoPanel> panels = new ArrayList<>();
    for (LocoModel locoModel : models) {
      LocoPanel panel = new LocoPanel();
      panel.setModel(locoModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<String> getLocoIds() {
    List<String> locos = new ArrayList<>();
    for (LocoPanel content : getContent()) {
      String id = content.getModel().getId();
      if (!(id.isBlank() || id.isEmpty())) {
        locos.add(id);
      }
    }
    return locos;
  }
}
