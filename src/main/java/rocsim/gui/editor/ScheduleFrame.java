package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel;

public class ScheduleFrame extends ListFrame<SchedulePanel> {
  private static final long serialVersionUID = 1L;
  private StringListDataModel blockIdDataModel;
  private TimeModel timeModel;

  private static class SchedulePanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<SchedulePanel> {
    private StringListDataModel blockIdDataModel;
    private TimeModel timeModel;

    public SchedulePanelFactory(TimeModel timeModel, StringListDataModel blockIdDataModel) {
      this.blockIdDataModel = blockIdDataModel;
    }

    @Override
    public SchedulePanel createNewItem() {
      return new SchedulePanel(this.timeModel, this.blockIdDataModel);
    }
  };

  public ScheduleFrame(TimeModel timeModel, StringListDataModel blockIdDataModel) {
    super(new SchedulePanelFactory(timeModel, blockIdDataModel));
    this.blockIdDataModel = blockIdDataModel;
    this.timeModel = timeModel;

  }

  public void setScheduleModels(List<ScheduleModel> models) {
    List<SchedulePanel> panels = new ArrayList<>();
    for (ScheduleModel scheduleModel : models) {
      SchedulePanel panel = new SchedulePanel(this.timeModel, this.blockIdDataModel);
      panel.setModel(scheduleModel);
      panels.add(panel);
    }
    setContent(panels);

  }

}
