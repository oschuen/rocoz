package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class LocoIdListDataModel implements ListModel<String> {

  private List<ListDataListener> dataListeners = new ArrayList<>();
  private List<String> locoIds = new ArrayList<>();

  /**
   * @param locoIds the locoIds to set
   */
  public void setLocoIds(List<String> locoIds) {
    this.locoIds = locoIds;
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
    if (index >= 0 & index < this.locoIds.size()) {
      return this.locoIds.get(index);
    }
    return "";
  }

  @Override
  public int getSize() {
    return this.locoIds.size();
  }

  public void fireDataChanged() {
    ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, this.locoIds.size());
    for (ListDataListener listDataListener : this.dataListeners) {
      listDataListener.contentsChanged(event);
    }
  }

}
