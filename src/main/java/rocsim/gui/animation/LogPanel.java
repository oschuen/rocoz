/*
 * Copyright © 2023 Oliver Schünemann (oschuen@users.noreply.github.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rocsim.gui.animation;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;

import rocsim.log.PlanLogAdapter;
import rocsim.log.PlanLogAdapter.LogEvent;
import rocsim.log.PlanLogAdapter.LogEventListener;
import rocsim.schedule.model.TimeModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;

public class LogPanel extends JPanel implements LogEventListener, TimeModelChangeListener {
  private static final long serialVersionUID = 1L;
  private JTable logTable;
  private TableModel model = new TableModel();
  private TimeModel incrModel;
  private int maxTime = 0;

  public LogPanel(TimeModel incrModel) {
    this.incrModel = incrModel;
    incrModel.addListener(this);
    setLayout(new BorderLayout(0, 0));

    PlanLogAdapter.setListener(this);

    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    add(scrollPane, BorderLayout.CENTER);

    this.logTable = new JTable(this.model);
    scrollPane.getViewport().add(this.logTable);
    this.logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    this.logTable.getColumnModel().getColumn(2).setPreferredWidth(800);
    this.logTable.getColumnModel().getColumn(0).setHeaderValue("Real Time");
    this.logTable.getColumnModel().getColumn(1).setHeaderValue("Fremo Time");
    this.logTable.getColumnModel().getColumn(2).setHeaderValue("Event");
  }

  private class TableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private Lock lock = new ReentrantLock();
    private List<LogEvent> events = new ArrayList<>();

    @Override
    public int getColumnCount() {
      return 3;
    }

    @Override
    public int getRowCount() {
      int count = 0;
      this.lock.lock();
      try {
        count = this.events.size();
      } finally {
        this.lock.unlock();
      }
      return count;
    }

    public void addEvent(LogEvent event) {
      this.lock.lock();
      try {
        if (LogPanel.this.maxTime >= event.time) {
          List<LogEvent> removes = new ArrayList<>();
          for (LogEvent logEvent : this.events) {
            if (logEvent.time >= event.time) {
              removes.add(logEvent);
            }
          }
          this.events.removeAll(removes);
        }
        this.events.add(event);
        LogPanel.this.maxTime = event.time;
      } finally {
        this.lock.unlock();
      }
      fireTableDataChanged();
    }

    @Override
    public Object getValueAt(int row, int column) {
      String value = "";
      this.lock.lock();
      try {
        if (row < this.events.size()) {
          LogEvent event = this.events.get(row);
          if (column == 0) {
            value = LogPanel.this.incrModel.getTimeSecString(event.time);

          } else if (column == 1) {
            value = LogPanel.this.incrModel.getFremoTimeSecString(event.time);

          } else {
            value = event.message;
          }
        }
      } finally {
        this.lock.unlock();
      }
      return value;
    }
  }

  @Override
  public void newLogEvent(LogEvent event) {
    this.model.addEvent(event);
  }

  @Override
  public void timeModelChanged() {
    this.model.fireTableDataChanged();
  }
}
