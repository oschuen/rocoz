package rocsim.gui.editor;

import java.util.ArrayList;
import java.util.List;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.LocoModel;

public class LocoFrame extends ListFrame<LocoPanel> {

  private static final long serialVersionUID = 1L;

  public LocoFrame() {
    super(new LocoPanel.LocoPanelFactory());
  }

  public void setLocoModels(List<LocoModel> models) {
    List<LocoPanel> panels = new ArrayList<>();
    for (LocoModel locoModel : models) {
      LocoPanel panel = new LocoPanel();
      panel.setModel(locoModel);
      panels.add(panel);
    }
    setContent(panels);
  }

  public List<String> getLocoIds() {
    List<String> locos = new ArrayList<>();
    for (LocoPanel content : getContent()) {
      String id = content.getModel().getId();
      if (!(id.isBlank() || id.isEmpty())) {
        locos.add(id);
      }
    }
    return locos;
  }
}
