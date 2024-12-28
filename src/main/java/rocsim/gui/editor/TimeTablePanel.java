package rocsim.gui.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

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
  private final int shuntingDistance = 60;
  private final int stationDistance = 150;
  private EditorContext context;
  private List<PlatformWidget> platforms = new ArrayList<>();
  private List<StationWidget> stations = new ArrayList<>();
  private List<ScheduleWidget> schedules = new ArrayList<>();
  private Map<String, PlatformWidget> platformMap = new HashMap<>();
  private int topTime = 4 * 3600 + 10 * 60;
  private int timeRadix = 4;
  private JPopupMenu editMenu;
  private JPopupMenu addMenu;
  private EditTripMouseAdapter mouseAdapter;
  private String lineId = "";
  private String locoId;
  private boolean isShuntingVisible;

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
    String locoId;
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
    setToolTipText("");
    this.editMenu = new JPopupMenu();
    JMenuItem myItem = new JMenuItem("Edit");
    this.editMenu.add(myItem);
    myItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        editTrip();
      }
    });
    JMenuItem menuItemDuplicate = new JMenuItem("Duplicate");
    this.editMenu.add(menuItemDuplicate);
    menuItemDuplicate.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        duplicateTrip();
      }
    });
    this.addMenu = new JPopupMenu();
    JMenuItem addItem = new JMenuItem("Add");
    this.addMenu.add(addItem);
    addItem.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        addTrip();
      }
    });

    this.mouseAdapter = new EditTripMouseAdapter();
    addMouseListener(this.mouseAdapter);
  }

  private int getTimeForY(int y) {
    return getTopTime() + (y - DRAW_ORIGIN) * this.timeRadix;
  }

  @Override
  public String getToolTipText(MouseEvent event) {
    int time = getTimeForY(event.getY());
    if (this.context.getTimeModel().isDisplayRealTime()) {
      return this.context.getTimeModel().getTimeSecString(time);
    }
    return this.context.getTimeModel().getTimeSecString(this.context.getTimeModel().toFremoTime(time));
  }

  private int addStation(StationModel stationModel, int sumX) {
    StationWidget station = new StationWidget();
    station.name = stationModel.getName();
    station.x = sumX;
    this.stations.add(station);
    boolean first = true;
    boolean shuntingBefore = false;
    for (PlatformModel platformModel : stationModel.getPlatforms()) {
      if (this.isShuntingVisible && platformModel.isShuntingBlock()) {
        if (!first) {
          sumX += this.shuntingDistance;
        }
        PlatformWidget platform = new PlatformWidget();
        platform.name = platformModel.getName();
        platform.x = sumX;
        this.platforms.add(platform);
        this.platformMap.put(platformModel.getBlockId(), platform);
        sumX += this.platformDistance;
        shuntingBefore = true;
      } else if (!platformModel.isShuntingBlock()) {
        if (shuntingBefore) {
          sumX += this.shuntingDistance;
          shuntingBefore = false;
        }
        PlatformWidget platform = new PlatformWidget();
        platform.name = platformModel.getName();
        platform.x = sumX;
        this.platforms.add(platform);
        this.platformMap.put(platformModel.getBlockId(), platform);
        sumX += this.platformDistance;
      }
      first = false;
    }

    sumX += this.stationDistance;
    return sumX;
  }

  public void setLoco(String locoId) {
    this.locoId = locoId;
    triggerRepaint();
  }

  public void setShuntingView(boolean shunting) {
    if (this.isShuntingVisible != shunting) {
      this.isShuntingVisible = shunting;
      setLine(this.lineId);
    }

  }

  public void setLine(String lineId) {
    boolean first = true;
    this.lineId = lineId;
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
          schedule.locoId = trip.getLocoId();
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
    Graphics g2 = gr.create(0, DRAW_ORIGIN, getWidth(), getHeight() - DRAW_ORIGIN);
    g2.translate(0, -this.topTime / this.timeRadix);

    for (ScheduleWidget schedule : this.schedules) {
      if (Objects.equals(this.locoId, schedule.locoId)) {
        int startY = schedule.startTime / this.timeRadix;
        int endY = schedule.endTime / this.timeRadix;
        int blockY = (schedule.endTime + 60) / this.timeRadix;
        g2.setColor(new Color(255, 255, 0));
        g2.fillRect(Math.min(schedule.startX, schedule.endX), Math.min(startY, endY),
            Math.abs(schedule.endX - schedule.startX), Math.abs(startY - blockY));
      }
    }

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

    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
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
      g2.setColor(Color.BLACK);
      int startY = schedule.startTime / this.timeRadix;
      int endY = schedule.endTime / this.timeRadix;
      int pauseY = (schedule.endTime + schedule.pauseTime) / this.timeRadix;
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

      g2.setColor(Color.BLACK);
      g2.drawString(schedule.id, centerX - adv / 2, centerY + adw / 2);
      Graphics2D g3 = (Graphics2D) g2.create();
      g3.setStroke(new BasicStroke(2));
      if (schedule.id.toLowerCase().startsWith("n")) {
        g3.setColor(Color.GREEN);
      } else if (schedule.id.toLowerCase().startsWith("D")) {
        g3.setColor(Color.BLUE);
      } else if (schedule.id.toLowerCase().startsWith("L")) {
        g3.setColor(Color.ORANGE);
      } else {
        g3.setColor(Color.BLACK);
      }
      if (schedule.pauseTime == 0) {
        g3.fillOval(schedule.endX - 3, endY - 3, 6, 6);
      } else {
        g3.drawLine(schedule.endX, endY, schedule.endX, pauseY);
      }
      Area area = new Area(new Rectangle2D.Double(Math.min(schedule.startX, schedule.endX), Math.min(startY, endY) - 4,
          Math.abs(schedule.endX - schedule.startX), Math.abs(startY - endY) + 8));
      area.subtract(new Area(new Rectangle2D.Double(centerX - adv / 2, centerY - adw / 2, adv, adw)));
      area.subtract(new Area(new Rectangle2D.Double(0, 0, getWidth(), this.topTime / this.timeRadix)));
      g3.setClip(area);
      g3.drawLine(schedule.startX, startY, schedule.endX, endY);
    }
  }

  class EditTripMouseAdapter extends MouseAdapter {
    private ScheduleWidget widget = new ScheduleWidget();

    @Override
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger()) {
        ScheduleWidget foundWidget = null;
        int time = getTimeForY(e.getY());
        for (ScheduleWidget scheduleWidget : TimeTablePanel.this.schedules) {
          int minX = Math.min(scheduleWidget.startX, scheduleWidget.endX);
          int maxX = Math.max(scheduleWidget.startX, scheduleWidget.endX);
          if (scheduleWidget.startTime <= time && scheduleWidget.endTime >= time && e.getX() >= minX
              && e.getX() <= maxX) {
            foundWidget = scheduleWidget;
            break;
          }
        }
        if (foundWidget != null) {
          this.widget = foundWidget;
          showPopup(e);
        } else {
          showAddPopup(e);
        }
      }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
      mousePressed(e);
    }

    private void showPopup(MouseEvent e) {
      TimeTablePanel.this.editMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void showAddPopup(MouseEvent e) {
      TimeTablePanel.this.addMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    /**
     * @return the widget
     */
    public ScheduleWidget getWidget() {
      return this.widget;
    }
  }

  private void editTrip() {
    ConfigTripDialog dialog = new ConfigTripDialog(this.context, this.mouseAdapter.getWidget().id, false);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    setLine(this.lineId);
  }

  private void duplicateTrip() {
    ConfigTripDialog dialog = new ConfigTripDialog(this.context, this.mouseAdapter.getWidget().id, true);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    setLine(this.lineId);
  }

  private void addTrip() {
    ConfigTripDialog dialog = new ConfigTripDialog(this.context, "", true);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    setLine(this.lineId);
  }

}
