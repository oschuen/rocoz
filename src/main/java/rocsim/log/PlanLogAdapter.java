package rocsim.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class PlanLogAdapter extends UnsynchronizedAppenderBase<ILoggingEvent> {

  static private LogEventListener listener = null;

  public interface LogEventListener {
    void newLogEvent(LogEvent event);
  }

  public static class LogEvent {
    public int time = 0;
    public String message = "";

    @Override
    public String toString() {
      return "LogEvent [time=" + this.time + ", message=" + this.message + "]";
    }
  }

  static public void setListener(LogEventListener listener) {
    PlanLogAdapter.listener = listener;
  }

  @Override
  protected void append(ILoggingEvent eventObject) {
    LogEvent event = new LogEvent();
    String complete = eventObject.getFormattedMessage();
    int index = complete.indexOf("[");
    int endIndex = complete.indexOf("]", index + 1);
    if (index >= 0 && endIndex > index) {
      event.time = Integer.valueOf(complete.substring(index + 1, endIndex));
    }
    event.message = complete.substring(endIndex + 1);
    javax.swing.SwingUtilities.invokeLater(() -> {
      if (listener != null) {
        listener.newLogEvent(event);
      }
    });
  }
}
