package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.io.IOUtils;

import rocsim.schedule.model.LineModel;
import rocsim.schedule.model.LineSegmentModel;
import rocsim.schedule.model.PlatformModel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.StationModel;
import rocsim.schedule.model.TripModel;

public class StationTrackDataPanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private EditorContext context;
  private JTextArea dataTextArea;
  private JComboBox<String> dataBox;
  private String template;

  public StationTrackDataPanel(EditorContext context) {
    this.context = context;
    setLayout(new BorderLayout(0, 0));

    this.dataTextArea = new JTextArea();
    this.dataTextArea.setText("data");
    add(this.dataTextArea, BorderLayout.CENTER);

    JPanel selectionPanel = new JPanel();
    String[] dataSelection = new String[] { "Stations", "Routes", "Lines", "Trains" };
    this.dataBox = new JComboBox<String>(dataSelection);
    selectionPanel.add(this.dataBox);
    updateContext();
    this.dataBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        updateContext();
      }
    });
    add(selectionPanel, BorderLayout.NORTH);
    try {
      this.template = IOUtils.toString(StationTrackDataPanel.class.getResourceAsStream("leer_xpln.csv"),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      this.template = "";
    }
  }

  public void updateContext() {
    switch (this.dataBox.getSelectedIndex()) {
    case 0:
      updateStations();
      break;
    case 1:
      updateRoutes();
      break;
    case 2:
      updateLines();
      break;
    case 3:
      updateTrains();
      break;
    default:
    }
  }

  private String getObjectId(String trainId) {
    String numbers = "01234356789";
    String number = "";
    for (int i = trainId.length() - 1; i >= 0; i--) {
      String c = trainId.substring(i, i + 1);
      if (numbers.contains(c)) {
        number = c + number;
      } else {
        break;
      }
    }
    return "000000".substring(number.length()) + number + "." + trainId;
  }

  private void updateTrains() {
    StringBuffer buffer = new StringBuffer();
    String startStation = "XXX";
    String endStation = "YYY";
    int startTime = 0;
    for (TripModel tripModel : this.context.getTripModels()) {
      if (tripModel.isShunting()) {
        continue;
      }
      int time = tripModel.getStartTime();
      startTime = time;
      startStation = tripModel.getStation();
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t0\t\t\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t80\t");
      buffer.append(getObjectId(tripModel.getId().replace(" ", "")));
      buffer.append("\ttraindef\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t\t\t\t\t\t\n");

      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t10\t");
      buffer.append(tripModel.getStation());
      buffer.append("\t");
      buffer.append(tripModel.getPlatform());
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t\t");
      buffer.append("\ttimetable\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t\t\t\t\t\t\n");
      int enVal = 10;
      for (ScheduleModel schedule : tripModel.getSchedules()) {
        enVal += 10;
        buffer.append(tripModel.getId().replace(" ", ""));
        buffer.append("\t" + enVal + "\t");
        Entry<String, String> interStation = this.context.getStationAndPlatform(schedule.getEndBlock());
        endStation = interStation.getKey();
        buffer.append(interStation.getKey());
        buffer.append("\t");
        buffer.append(interStation.getValue());
        buffer.append("\t");
        time = time + schedule.getDuration();
        buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
        buffer.append("\t");
        time = time + schedule.getPause();
        buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
        buffer.append("\t\t");
        buffer.append("\ttimetable\t");
        buffer.append(tripModel.getId().replace(" ", ""));
        buffer.append("\t\t\t\t\t\t\n");
      }
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t1000\t");
      buffer.append(startStation);
      buffer.append("\t");
      buffer.append(endStation);
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(startTime));
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t\t");
      buffer.append(tripModel.getLocoId());
      buffer.append("\tlocomotive\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t\t\t\t\t\t\n");

      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t3000\t");
      buffer.append(startStation);
      buffer.append("\t");
      buffer.append(endStation);
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(startTime));
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\tjob\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t\t\t\t\t\t\n");

      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t4000\t");
      buffer.append(startStation);
      buffer.append("\t");
      buffer.append(endStation);
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(startTime));
      buffer.append("\t");
      buffer.append(this.context.getTimeModel().getFremoTimeMinString(time));
      buffer.append("\t0\t");
      buffer.append("\twheel\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t\t\t\t\t\t\n");

      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t5000\t");
      buffer.append("\t");
      buffer.append("\t");
      buffer.append("00:00");
      buffer.append("\t");
      buffer.append("00:00");
      buffer.append("\t0\tunb");
      buffer.append("\tgroup\t");
      buffer.append(tripModel.getId().replace(" ", ""));
      buffer.append("\t\t\t\t\t\t\n");
    }
    this.dataTextArea.setText(buffer.toString());
  }

  private void updateLines() {
    StringBuffer buffer = new StringBuffer();
    boolean firstStation = true;
    for (LineModel lineModel : this.context.getLineModels()) {
      if (firstStation) {
        firstStation = false;
      } else {
        buffer.append("\n");
      }
      buffer.append("Default\tLine\t");
      buffer.append(lineModel.getName());
      buffer.append("\t \t6\t \t \t");
      buffer.append(lineModel.getName());
      String start = "XXX";
      String end = "XXX";
      boolean first = true;
      for (LineSegmentModel segment : lineModel.getLineSegments()) {
        if (first) {
          first = false;
          start = segment.getStationOne();
        }
        end = segment.getStationTwo();
      }
      buffer.append("\t" + start + "\t" + end + "\t");
      buffer.append(String.format("%1.2f", (this.context.getTimeModel().getMinTime() / 3600 / 24.0F)));
      buffer.append("\t");
      buffer.append(String.format("%1.2f",
          (this.context.getTimeModel().getRadix() * this.context.getTimeModel().getMaxTime() / 3600 / 24.0F)));
      buffer.append("\t1\t300\tStation,Main\t100\t100\t5\t \t");
    }
//    String text = new String(this.template);
//    text = text.replace("[LINES]", buffer.toString());
//    this.dataTextArea.setText(text);
    this.dataTextArea.setText(buffer.toString());
  }

  private void updateRoutes() {
    StringBuffer buffer = new StringBuffer();
    int index = 0;
    for (LineModel lineModel : this.context.getLineModels()) {
      int pos = 0;
      for (LineSegmentModel segment : lineModel.getLineSegments()) {
        buffer.append(index);
        buffer.append("\t \t");
        buffer.append(segment.getStationOne() + "\t");
        buffer.append(pos + ",0\t");
        buffer.append(segment.getStationTwo() + "\t");
        buffer.append((pos + 1) + ",0\t");
        buffer.append("60\t1\t4\t");
        StationModel firstStation = this.context.getStationModel(segment.getStationOne());
        if (firstStation.getPlatforms().isEmpty()) {
          buffer.append("1");
        } else {
          buffer.append(firstStation.getPlatforms().get(0).getName());
        }
        buffer.append("\t");
        StationModel secondStation = this.context.getStationModel(segment.getStationTwo());
        if (secondStation.getPlatforms().isEmpty()) {
          buffer.append("1");
        } else {
          buffer.append(secondStation.getPlatforms().get(0).getName());
        }
        buffer.append("\n");
        pos++;
        index++;
      }
    }
    this.dataTextArea.setText(buffer.toString());
  }

  private void updateStations() {
    StringBuffer buffer = new StringBuffer();
    for (StationModel station : this.context.getStationModels()) {
      int enValue = 0;
      buffer.append(station.getName());
      buffer.append("\t");
      buffer.append(enValue);
      buffer.append("\t\t\t");
      buffer.append(station.getName());
      buffer.append("\tStation\tStation\t");
      buffer.append("\n");

      for (PlatformModel platform : station.getPlatforms()) {
        if (!platform.isShuntingBlock()) {
          enValue++;
          buffer.append(station.getName());
          buffer.append("\t");
          buffer.append(enValue);
          buffer.append("\t");
          buffer.append(platform.getName());
          buffer.append("\t200\t");
          buffer.append("\tTrack\tMain\t");
          buffer.append("\n");
        }
      }
    }
    this.dataTextArea.setText(buffer.toString());
  }
}
