package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.ScheduleModel;

public class ScheduleFrame extends ListFrame<SchedulePanel> {
  private static final long serialVersionUID = 1L;
  private StringListDataModel blockIdDataModel;

  private static class SchedulePanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<SchedulePanel> {
    private StringListDataModel blockIdDataModel;

    public SchedulePanelFactory(StringListDataModel blockIdDataModel) {
      this.blockIdDataModel = blockIdDataModel;
    }

    @Override
    public SchedulePanel createNewItem() {
      return new SchedulePanel(this.blockIdDataModel);
    }
  };

  public ScheduleFrame(StringListDataModel blockIdDataModel) {
    super(new SchedulePanelFactory(blockIdDataModel));
    this.blockIdDataModel = blockIdDataModel;
  }

  public void setScheduleModels(List<ScheduleModel> models) {
    List<SchedulePanel> panels = new ArrayList<>();
    for (ScheduleModel scheduleModel : models) {
      SchedulePanel panel = new SchedulePanel(this.blockIdDataModel);
      panel.setModel(scheduleModel);
      panels.add(panel);
    }
    setContent(panels);

  }

}
