package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.LineModel;

public class LineFrame extends ListFrame<LinePanel> {

  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class LinePanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<LinePanel> {

    private EditorContext context;

    LinePanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public LinePanel createNewItem() {
      return new LinePanel(this.context);
    }
  };

  public LineFrame(EditorContext context) {
    super(new LinePanelFactory(context));
    this.context = context;
  }

  public void setLineModels(List<LineModel> lineModels) {
    List<LinePanel> panels = new ArrayList<>();
    for (LineModel lineModel : lineModels) {
      LinePanel panel = new LinePanel(this.context);
      panel.setModel(lineModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<LineModel> getLineModels() {
    List<LineModel> models = new ArrayList<>();
    for (LinePanel line : getContent()) {
      models.add(line.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (LinePanel line : getContent()) {
      line.updateContext();
    }
  }
}
