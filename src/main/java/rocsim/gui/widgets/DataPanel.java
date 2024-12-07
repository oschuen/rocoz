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
package rocsim.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class DataPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private List<ListDataListener> dataListeners = new ArrayList<>();

  public void addDataListener(ListDataListener listener) {
    this.dataListeners.add(listener);
  }

  public void removeDataListener(ListDataListener listener) {
    this.dataListeners.remove(listener);
  }

  public void fireDataChanged() {
    ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, 0);
    for (ListDataListener listDataListener : this.dataListeners) {
      listDataListener.contentsChanged(event);
    }
  }

}
