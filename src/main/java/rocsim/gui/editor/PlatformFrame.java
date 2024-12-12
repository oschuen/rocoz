package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.PlatformModel;

public class PlatformFrame extends ListFrame<PlatformPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class PlatformPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<PlatformPanel> {
    private EditorContext context;

    PlatformPanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public PlatformPanel createNewItem() {
      return new PlatformPanel(this.context);
    }
  };

  public PlatformFrame(EditorContext context) {
    super(new PlatformPanelFactory(context));
    this.context = context;

  }

  public void setPlatformModels(List<PlatformModel> models) {
    List<PlatformPanel> panels = new ArrayList<>();
    for (PlatformModel platformModel : models) {
      PlatformPanel panel = new PlatformPanel(this.context);
      panel.setModel(platformModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<PlatformModel> getPlatformModels() {
    List<PlatformModel> models = new ArrayList<>();
    for (PlatformPanel platform : getContent()) {
      models.add(platform.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (PlatformPanel platform : getContent()) {
      platform.updateContext();
    }
  }
}
