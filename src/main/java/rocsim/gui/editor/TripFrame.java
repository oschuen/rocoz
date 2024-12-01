package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;
import rocsim.schedule.model.TripModel;

public class TripFrame extends ListFrame<TripPanel> {

  private static final long serialVersionUID = 1L;

  private TimeModel timeModel;

  private TimeModelChangeListener timeChangeListener = new TimeModelChangeListener() {

    @Override
    public void timeModelChanged() {
      for (TripPanel tripPanel : TripFrame.this.getContent()) {
        tripPanel.adjustTime();
      }
    }
  };

  private static class TripPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<TripPanel> {
    private TimeModel timeModel;

    TripPanelFactory(TimeModel timeModel) {
      this.timeModel = timeModel;
    }

    @Override
    public TripPanel createNewItem() {
      return new TripPanel(this.timeModel);
    }
  };

  public TripFrame(TimeModel timeModel) {
    super(new TripPanelFactory(timeModel));
    this.timeModel = timeModel;
    this.timeModel.addListener(TripFrame.this.timeChangeListener);
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
      TripPanel panel = new TripPanel(this.timeModel);
      panel.setLocoIdComboBoxModel(new LocoIdComboBoxModel(this.locoIdDataModel));
      panel.setModel(tripModel);
      panels.add(panel);
    }
    setContent(panels);
  }
}
