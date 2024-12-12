package rocsim.gui.editor;

import java.util.List;

public interface EditorContext {
  List<String> getArrivableBlockIds();

  List<String> getWatchBlockIds();

  List<String> getStationNames();
}
