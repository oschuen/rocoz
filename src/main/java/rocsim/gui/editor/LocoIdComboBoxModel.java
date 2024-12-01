package rocsim.gui.editor;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

class LocoIdComboBoxModel implements ComboBoxModel<String> {

  private LocoIdListDataModel selectionModel;
  private String selectedLocoId = "";

  LocoIdComboBoxModel(LocoIdListDataModel selectionModel) {
    this.selectionModel = selectionModel;
  }

  @Override
  public void addListDataListener(ListDataListener arg0) {
    this.selectionModel.addListDataListener(arg0);

  }

  @Override
  public String getElementAt(int arg0) {
    return this.selectionModel.getElementAt(arg0);
  }

  @Override
  public int getSize() {
    return this.selectionModel.getSize();
  }

  @Override
  public void removeListDataListener(ListDataListener arg0) {
    this.selectionModel.removeListDataListener(arg0);

  }

  @Override
  public String getSelectedItem() {
    return this.selectedLocoId;
  }

  @Override
  public void setSelectedItem(Object arg0) {
    this.selectedLocoId = (String) arg0;

  }

}