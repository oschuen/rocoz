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
package rocsim.schedule.model;

import java.util.ArrayList;
import java.util.List;

public class TimeModel {
  private int radix = 1;
  private int currentTime = 0;
  private int minTime = 0;
  private int maxTime = 0;
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

  public int convertTimeString(String time) {
    int convTime = 0;
    int max = 0;
    String[] timeElem = time.split(":");
    for (int i = 0; i < timeElem.length && max < 3; i++) {
      convTime = convTime * 60;
      try {
        convTime += Integer.valueOf(timeElem[i]);
      } catch (Exception e) {
      }
      max++;
    }
    return convTime;

  }

  public int toRealTime(int fremoTime) {
    return this.minTime + (fremoTime - this.minTime) / this.radix;
  }

  public int getFremoTime() {
    return this.minTime + (this.currentTime - this.minTime) * this.radix;
  }

  public String getTimeSecString(int time) {
    int temp = (time % 86400) + 86400;
    int hour = (temp % 86400) / 3600;
    int min = (temp % 3600) / 60;
    int sec = temp % 60;
    return String.format("%02d:%02d:%02d", hour, min, sec);
  }

  public String getFremoTimeSecString(int realTime) {
    int time = this.minTime + (realTime - this.minTime) * this.radix;
    return getTimeSecString(time);
  }

  public String getTimeMinString(int time) {
    int temp = (time % 86400) + 86400;
    int hour = (temp % 86400) / 3600;
    int min = (temp % 3600) / 60;
    return String.format("%02d:%02d", hour, min);
  }

  public String getFremoTimeMinString(int realTime) {
    int time = this.minTime + (realTime - this.minTime) * this.radix;
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

  public int incrementCurrentTime() {
    return ++this.currentTime;
  }

  /**
   * @return the base
   */
  public int getMinTime() {
    return this.minTime;
  }

  /**
   * @param base the base to set
   */
  public void setMinTime(int base) {
    if (this.minTime != base) {
      this.minTime = base;
      notifyListeners();
    }
  }

  /**
   * @return the maxTime
   */
  public int getMaxTime() {
    return this.maxTime;
  }

  /**
   * @param maxTime the maxTime to set
   */
  public void setMaxTime(int maxTime) {
    this.maxTime = maxTime;
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
