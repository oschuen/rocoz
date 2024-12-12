package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.LineSegmentModel;

public class LineSegmentFrame extends ListFrame<LineSegmentPanel> {
  private static final long serialVersionUID = 1L;
  private EditorContext context;

  private static class LineSegmentPanelFactory
      implements rocsim.gui.widgets.ListFrame.ListItemFactory<LineSegmentPanel> {
    private EditorContext context;

    public LineSegmentPanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public LineSegmentPanel createNewItem() {
      return new LineSegmentPanel(this.context);
    }
  };

  public LineSegmentFrame(EditorContext context) {
    super(new LineSegmentPanelFactory(context));
    this.context = context;
  }

  public void setLineSegmentModels(List<LineSegmentModel> models) {
    List<LineSegmentPanel> panels = new ArrayList<>();
    for (LineSegmentModel segmentModel : models) {
      LineSegmentPanel panel = new LineSegmentPanel(this.context);
      panel.setModel(segmentModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<LineSegmentModel> getLineSegmentModels() {
    List<LineSegmentModel> models = new ArrayList<>();
    for (LineSegmentPanel segement : getContent()) {
      models.add(segement.getModel());
    }
    return models;
  }

  public void updateContext() {
    for (LineSegmentPanel segment : getContent()) {
      segment.updateContext();
    }

  }
}
