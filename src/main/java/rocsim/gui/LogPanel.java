package rocsim.gui;

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

import rocsim.gui.model.IncrementModel;
import rocsim.gui.model.IncrementModel.IncrementModelChangeListener;
import rocsim.log.PlanLogAdapter;
import rocsim.log.PlanLogAdapter.LogEvent;
import rocsim.log.PlanLogAdapter.LogEventListener;

public class LogPanel extends JPanel implements LogEventListener, IncrementModelChangeListener {
  private static final long serialVersionUID = 1L;
  private JTable logTable;
  private TableModel model = new TableModel();
  private IncrementModel incrModel;

  public LogPanel(IncrementModel incrModel) {
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
      int row = 0;
      this.lock.lock();
      try {
        this.events.add(event);
        row = this.events.size();
      } finally {
        this.lock.unlock();
      }
      fireTableRowsInserted(row - 1, row - 1);
    }

    private String timeString(int time) {
      int hour = time / 3600;
      int min = (time % 3600) / 60;
      int sec = time % 60;
      return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    @Override
    public Object getValueAt(int row, int column) {
      String value = "";
      this.lock.lock();
      try {
        if (row < this.events.size()) {
          LogEvent event = this.events.get(row);
          if (column == 0) {
            value = timeString(event.time);

          } else if (column == 1) {
            value = timeString(event.time * LogPanel.this.incrModel.getFremoTimeIncrement());

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
  public void modelChanged(IncrementModel model) {
    this.model.fireTableDataChanged();
  }
}
