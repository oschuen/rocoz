package rocsim.gui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import rocsim.schedule.model.LineModel;
import rocsim.schedule.model.LineSegmentModel;
import rocsim.schedule.model.PlatformModel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.StationModel;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;
import rocsim.schedule.model.TripModel;

public class TimeTablePanel extends JPanel {
  private static final int DRAW_ORIGIN = 40;
  private static final long serialVersionUID = 1L;
  private final int platformDistance = 20;
  private final int stationDistance = 150;
  private EditorContext context;
  private List<PlatformWidget> platforms = new ArrayList<>();
  private List<StationWidget> stations = new ArrayList<>();
  private List<ScheduleWidget> schedules = new ArrayList<>();
  private Map<String, PlatformWidget> platformMap = new HashMap<>();
  private int topTime = 4 * 3600 + 10 * 60;
  private int timeRadix = 6;

  private class PlatformWidget {
    int x;
    String name;
  }

  private class StationWidget {
    int x;
    String name;
  }

  private class ScheduleWidget {
    int startTime;
    int endTime;
    int pauseTime;
    int startX;
    int endX;
    String id;
    boolean displayDeparture;
  }

  private void triggerRepaint() {
    javax.swing.SwingUtilities.invokeLater(() -> repaint());
  }

  public TimeTablePanel(EditorContext context) {
    this.context = context;
    context.getTimeModel().addListener(new TimeModelChangeListener() {
      @Override
      public void timeModelChanged() {
        triggerRepaint();
      }
    });
  }

  private int addStation(StationModel stationModel, int sumX) {
    StationWidget station = new StationWidget();
    station.name = stationModel.getName();
    station.x = sumX;
    this.stations.add(station);
    for (PlatformModel platformModel : stationModel.getPlatforms()) {
      PlatformWidget platform = new PlatformWidget();
      platform.name = platformModel.getName();
      platform.x = sumX;
      this.platforms.add(platform);
      this.platformMap.put(platformModel.getBlockId(), platform);
      sumX += this.platformDistance;
    }

    sumX += this.stationDistance;
    return sumX;
  }

  public void setLine(String lineId) {
    boolean first = true;
    this.stations.clear();
    this.platforms.clear();
    this.platformMap.clear();
    this.schedules.clear();
    int sumX = 100;
    LineModel lineModel = this.context.getLineModel(lineId);
    for (LineSegmentModel lineSegment : lineModel.getLineSegments()) {
      if (first) {
        StationModel stationModel = this.context.getStationModel(lineSegment.getStationOne());
        sumX = addStation(stationModel, sumX);
        first = false;
      }
      StationModel stationModel = this.context.getStationModel(lineSegment.getStationTwo());
      sumX = addStation(stationModel, sumX);
    }
    for (TripModel trip : this.context.getTripModels()) {
      String tripName = trip.getId();
      boolean firstSchedule = true;
      int tripTime = trip.getStartTime();
      for (ScheduleModel segment : trip.getSchedules()) {
        PlatformWidget one = this.platformMap.get(segment.getStartBlock());
        PlatformWidget two = this.platformMap.get(segment.getEndBlock());
        if (one != null && two != null) {
          ScheduleWidget schedule = new ScheduleWidget();
          schedule.startX = one.x;
          schedule.endX = two.x;
          schedule.startTime = tripTime;
          schedule.endTime = tripTime + segment.getDuration();
          schedule.pauseTime = segment.getPause();
          schedule.id = tripName;
          schedule.displayDeparture = firstSchedule;
          tripTime += segment.getDuration() + segment.getPause();
          this.schedules.add(schedule);
          firstSchedule = (segment.getPause() > 0);
        }
      }
    }

    triggerRepaint();
  }

  /**
   * @return the topTime
   */
  public int getTopTime() {
    return this.topTime;
  }

  /**
   * @param topTime the topTime to set
   */
  public void setTopTime(int topTime) {
    if (this.topTime != topTime) {
      this.topTime = topTime;
      triggerRepaint();
    }
  }

  @Override
  public void paint(Graphics graphics) {
    super.paint(graphics);
    TimeModel timeModel = this.context.getTimeModel();

    Graphics2D gr = (Graphics2D) graphics;
    gr.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
    gr.setColor(Color.BLACK);
    for (StationWidget station : this.stations) {
      gr.drawString(station.name, station.x, 18);
    }
    gr.setStroke(new BasicStroke(3));
    for (PlatformWidget platform : this.platforms) {
      gr.setColor(Color.BLACK);
      gr.drawString(platform.name, platform.x, 36);
      gr.setColor(Color.PINK);
      gr.drawLine(platform.x, DRAW_ORIGIN, platform.x, getHeight());
    }
    Graphics g2 = gr.create(0, DRAW_ORIGIN, getWidth(), getHeight() - DRAW_ORIGIN);
    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
    g2.translate(0, -this.topTime / this.timeRadix);
    int hour = this.topTime - this.topTime % 3600;
    if (timeModel.isDisplayRealTime()) {
      int timeY = hour / this.timeRadix;
      while (timeY < this.topTime / this.timeRadix + getHeight() - DRAW_ORIGIN) {
        g2.setColor(Color.PINK);
        g2.drawLine(0, timeY, getWidth(), timeY);
        g2.setColor(Color.BLACK);
        g2.drawString(timeModel.getTimeMinString(hour), this.platformDistance, timeY);
        hour += 3600;
        timeY = (hour) / this.timeRadix;
      }
    } else {
      int fremoTime = timeModel.toFremoTime(hour) - timeModel.toFremoTime(hour) % 3600;
      int timeY = (timeModel.toRealTime(fremoTime)) / this.timeRadix;
      while (timeY < this.topTime / this.timeRadix + getHeight() - DRAW_ORIGIN) {
        g2.setColor(Color.PINK);
        g2.drawLine(0, timeY, getWidth(), timeY);
        g2.setColor(Color.BLACK);
        g2.drawString(timeModel.getTimeMinString(fremoTime), this.platformDistance, timeY);
        fremoTime += 3600;
        timeY = (timeModel.toRealTime(fremoTime)) / this.timeRadix;
      }
    }

    for (ScheduleWidget schedule : this.schedules) {
      if (schedule.id.toLowerCase().startsWith("n")) {
        g2.setColor(Color.GREEN);
      } else if (schedule.id.toLowerCase().startsWith("D")) {
        g2.setColor(Color.BLUE);
      } else if (schedule.id.toLowerCase().startsWith("L")) {
        g2.setColor(Color.ORANGE);
      } else {
        g2.setColor(Color.BLACK);
      }

      int startY = schedule.startTime / this.timeRadix;
      int endY = schedule.endTime / this.timeRadix;
      int blockY = (schedule.endTime + 60) / this.timeRadix;
      int pauseY = (schedule.endTime + schedule.pauseTime) / this.timeRadix;
      g2.drawLine(schedule.startX, startY, schedule.endX, endY);
      if (schedule.pauseTime == 0) {
        g2.fillOval(schedule.endX - 3, endY - 3, 6, 6);
      } else {
        g2.drawLine(schedule.endX, endY, schedule.endX, pauseY);
      }
      int minEnd;
      int minStart;
      if (timeModel.isDisplayRealTime()) {
        minStart = (schedule.startTime % 3600) / 60;
        minEnd = (schedule.endTime % 3600) / 60;
      } else {
        minStart = (timeModel.toFremoTime(schedule.startTime) % 3600) / 60;
        minEnd = (timeModel.toFremoTime(schedule.endTime) % 3600) / 60;
      }
      g2.setColor(Color.BLACK);
      g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
      FontMetrics smallMetrics = g2.getFontMetrics(g2.getFont());
      String strMinStart = Integer.toString(minStart);
      int minuteStartWidth = smallMetrics.stringWidth(strMinStart);
      String strMinEnd = Integer.toString(minEnd);
      int minuteEndWidth = smallMetrics.stringWidth(strMinEnd);

      if (schedule.startX < schedule.endX) {
        minuteStartWidth = 0;
      } else {
        minuteEndWidth = 0;
      }

      if (schedule.displayDeparture) {
        g2.drawString(Integer.toString(minStart), schedule.startX - minuteStartWidth, startY - 3);
      }
      g2.drawString(strMinEnd, schedule.endX - minuteEndWidth, endY - 3);
      int centerX = (schedule.startX + schedule.endX) / 2;
      int centerY = (startY + endY) / 2;
      g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
      FontMetrics metrics = g2.getFontMetrics(g2.getFont());
      int adv = metrics.stringWidth(schedule.id);
      int adw = metrics.getHeight();
      g2.setColor(getBackground());
      g2.fillRect(centerX - adv / 2, centerY - adw / 2, adv, adw);
      g2.setColor(Color.BLACK);
      g2.drawString(schedule.id, centerX - adv / 2, centerY + adw / 2);
      g2.setColor(new Color(255, 255, 0, 100));
      g2.fillRect(Math.min(schedule.startX, schedule.endX), Math.min(startY, endY),
          Math.abs(schedule.endX - schedule.startX), Math.abs(startY - blockY));
    }
  }
}
