package rocsim.gui.editor;

import rocsim.gui.widgets.ListFrame;

public class ScheduleFrame extends ListFrame<SchedulePanel> {
  private static final long serialVersionUID = 1L;

  private static class SchedulePanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<SchedulePanel> {

    @Override
    public SchedulePanel createNewItem() {
      return new SchedulePanel();
    }
  };

  public ScheduleFrame() {
    super(new SchedulePanelFactory());
  }

}
