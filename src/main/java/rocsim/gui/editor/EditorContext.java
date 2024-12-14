package rocsim.gui.editor;

import java.util.List;

import rocsim.schedule.model.TimeModel;

public interface EditorContext {
  List<String> getArrivableBlockIds();

  List<String> getWatchBlockIds();

  List<String> getStationNames();

  List<String> getPlatforms(String station);

  TimeModel getTimeModel();

  List<String> getLocoIds();

  String getBlockForPlatform(String station, String platform);
}
