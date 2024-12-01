package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.TripModel;

public class TripFrame extends ListFrame<TripPanel> {

  private static final long serialVersionUID = 1L;

  public TripFrame() {
    super(new TripPanel.TripPanelFactory());
  }

  LocoIdListDataModel locoIdDataModel = new LocoIdListDataModel();

  public void setLocoIds(List<String> locoIds) {
    this.locoIdDataModel.setLocoIds(locoIds);
    for (TripPanel panel : getContent()) {
      panel.setLocoIdComboBoxModel(new LocoIdComboBoxModel(this.locoIdDataModel));
    }
  }

  public void setTripModels(List<TripModel> tripModels) {
    List<TripPanel> panels = new ArrayList<>();
    for (TripModel tripModel : tripModels) {
      TripPanel panel = new TripPanel();
      panel.setLocoIdComboBoxModel(new LocoIdComboBoxModel(this.locoIdDataModel));
      panel.setModel(tripModel);
      panels.add(panel);
    }
    setContent(panels);

  }

//    void setLocoModels(List<LocoModel> models) {
//    List<LocoPanel> panels = new ArrayList<>();
//    for (LocoModel locoModel : models) {
//      LocoPanel panel = new LocoPanel();
//      panel.setModel(locoModel);
//      panels.add(panel);
//    }
//    setContent(panels);
//  }
}
