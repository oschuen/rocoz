package rocsim.schedule.model;

import java.util.ArrayList;
import java.util.List;

public class TimeModel {
  private int radix = 1;
  private int currentTime = 0;
  private int base = 0;
  private int increment = 1;

  private List<TimeModelChangeListener> listeners = new ArrayList<>();

  public interface TimeModelChangeListener {
    void timeModelChanged();
  }

  private void notifyListeners() {
    for (TimeModelChangeListener incrementModelChangeListener : this.listeners) {
      incrementModelChangeListener.timeModelChanged();
    }
  }

  public void addListener(TimeModelChangeListener listener) {
    this.listeners.add(listener);
  }

  public int getFremoTime() {
    return this.base + (this.currentTime - this.base) * this.radix;
  }

  public String getTimeSecString(int time) {
    int hour = (time % 86400) / 3600;
    int min = (time % 3600) / 60;
    int sec = time % 60;
    return String.format("%02d:%02d:%02d", hour, min, sec);
  }

  public String getFremoTimeSecString(int realTime) {
    int time = this.base + (realTime - this.base) * this.radix;
    return getTimeSecString(time);
  }

  public String getTimeMinString(int time) {
    int hour = (time % 86400) / 3600;
    int min = (time % 3600) / 60;
    return String.format("%02d:%02d", hour, min);
  }

  public String getFremoTimeMinString(int realTime) {
    int time = this.base + (realTime - this.base) * this.radix;
    return getTimeMinString(time);
  }

  /**
   * @return the radix
   */
  public int getRadix() {
    return this.radix;
  }

  /**
   * @param radix the radix to set
   */
  public void setRadix(int radix) {
    if (this.radix != radix) {
      this.radix = radix;
      notifyListeners();
    }
  }

  /**
   * @return the currentTime
   */
  public int getCurrentTime() {
    return this.currentTime;
  }

  /**
   * @param currentTime the currentTime to set
   */
  public void setCurrentTime(int currentTime) {
    this.currentTime = currentTime;
  }

  /**
   * @return the base
   */
  public int getBase() {
    return this.base;
  }

  /**
   * @param base the base to set
   */
  public void setBase(int base) {
    if (this.base != base) {
      this.base = base;
      notifyListeners();
    }
  }

  /**
   * @return the increment
   */
  public int getIncrement() {
    return this.increment;
  }

  /**
   * @param increment the increment to set
   */
  public void setIncrement(int increment) {
    if (increment != this.increment) {
      this.increment = increment;
      notifyListeners();
    }
  }
}
