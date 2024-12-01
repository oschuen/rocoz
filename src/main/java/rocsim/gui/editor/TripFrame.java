package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.TripModel;
import rocsim.schedule.time.TimeModel;
import rocsim.schedule.time.TimeModel.TimeModelChangeListener;

public class TripFrame extends ListFrame<TripPanel> {
  private static final long serialVersionUID = 1L;
  private StringListDataModel blockIdDataModel;
  private StringListDataModel locoIdDataModel;
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
    private StringListDataModel blockIdDataModel;
    private StringListDataModel locoIdDataModel;

    TripPanelFactory(TimeModel timeModel, StringListDataModel locoIdDataModel, StringListDataModel blockIdDataModel) {
      this.timeModel = timeModel;
      this.locoIdDataModel = locoIdDataModel;
      this.blockIdDataModel = blockIdDataModel;
    }

    @Override
    public TripPanel createNewItem() {
      return new TripPanel(this.timeModel, this.locoIdDataModel, this.blockIdDataModel);
    }
  };

  public TripFrame(TimeModel timeModel, StringListDataModel locoIdDataModel, StringListDataModel blockIdDataModel) {
    super(new TripPanelFactory(timeModel, locoIdDataModel, blockIdDataModel));
    this.blockIdDataModel = blockIdDataModel;
    this.locoIdDataModel = locoIdDataModel;
    this.timeModel = timeModel;
    this.timeModel.addListener(TripFrame.this.timeChangeListener);
  }

  public void setTripModels(List<TripModel> tripModels) {
    List<TripPanel> panels = new ArrayList<>();
    for (TripModel tripModel : tripModels) {
      TripPanel panel = new TripPanel(this.timeModel, this.locoIdDataModel, this.blockIdDataModel);
      panel.setModel(tripModel);
      panels.add(panel);
    }
    setContent(panels);
  }
}
