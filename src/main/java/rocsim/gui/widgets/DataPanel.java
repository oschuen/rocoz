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
