package rocsim.gui.model;

import java.util.ArrayList;
import java.util.List;

public class IncrementModel {

  private int increment = 1;
  private int fremoTimeIncrement = 1;
  private List<IncrementModelChangeListener> listeners = new ArrayList<>();

  public interface IncrementModelChangeListener {
    void modelChanged(IncrementModel model);
  }

  private void notifyListeners() {
    for (IncrementModelChangeListener incrementModelChangeListener : this.listeners) {
      incrementModelChangeListener.modelChanged(this);
    }
  }

  public void addListener(IncrementModelChangeListener listener) {
    this.listeners.add(listener);
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

  /**
   * @return the fremoTimeIncrement
   */
  public int getFremoTimeIncrement() {
    return this.fremoTimeIncrement;
  }

  /**
   * @param fremoTimeIncrement the fremoTimeIncrement to set
   */
  public void setFremoTimeIncrement(int fremoTimeIncrement) {
    if (this.fremoTimeIncrement != fremoTimeIncrement) {
      this.fremoTimeIncrement = fremoTimeIncrement;
      notifyListeners();
    }
  }
}
