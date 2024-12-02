package rocsim.gui.model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class StringListDataModel implements ListModel<String> {

  private List<ListDataListener> dataListeners = new ArrayList<>();
  private List<String> values = new ArrayList<>();

  /**
   * @param values the locoIds to set
   */
  public void setValueList(List<String> values) {
    this.values = values;
    fireDataChanged();
  }

  @Override
  public void addListDataListener(ListDataListener listener) {
    this.dataListeners.add(listener);
  }

  @Override
  public void removeListDataListener(ListDataListener listener) {
    this.dataListeners.remove(listener);
  }

  @Override
  public String getElementAt(int index) {
    if (index >= 0 & index < this.values.size()) {
      return this.values.get(index);
    }
    return "";
  }

  @Override
  public int getSize() {
    return this.values.size();
  }

  public void fireDataChanged() {
    ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.values.size());
    for (ListDataListener listDataListener : this.dataListeners) {
      listDataListener.contentsChanged(event);
    }
  }

}
