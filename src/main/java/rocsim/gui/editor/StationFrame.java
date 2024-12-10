package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.StationModel;

public class StationFrame extends ListFrame<StationPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class StationFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<StationPanel> {
    private EditorContext context;

    public StationFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public StationPanel createNewItem() {
      return new StationPanel(this.context);
    }
  };

  public StationFrame(EditorContext context) {
    super(new StationFactory(context));
    this.context = context;
  }

  public void setStationModels(List<StationModel> stationModels) {
    List<StationPanel> panels = new ArrayList<>();
    for (StationModel stationModel : stationModels) {
      StationPanel panel = new StationPanel(this.context);
      panel.setModel(stationModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<StationModel> getStationModels() {
    List<StationModel> models = new ArrayList<>();
    for (StationPanel station : getContent()) {
      models.add(station.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (StationPanel station : getContent()) {
      station.updateContext();
    }
  }
}
