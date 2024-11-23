package rocsim.schedule;

public class Schedule {
  private String startBlock = "";
  private String endBlock = "";
  private int startTime = 0;
  private int endTime = 0;
  private int minWaitTime = 0;

  public Schedule(String startBlock, String endBlock, int startTime, int endTime, int minWaitTime) {
    super();
    this.startBlock = startBlock;
    this.endBlock = endBlock;
    this.startTime = startTime;
    this.endTime = endTime;
    this.minWaitTime = minWaitTime;
  }

  /**
   * @return the startBlock
   */
  public String getStartBlock() {
    return this.startBlock;
  }

  /**
   * @return the endBlock
   */
  public String getEndBlock() {
    return this.endBlock;
  }

  /**
   * @return the startTime
   */
  public int getStartTime() {
    return this.startTime;
  }

  /**
   * @return the endTime
   */
  public int getEndTime() {
    return this.endTime;
  }

  /**
   * @return the minWaitTime
   */
  public int getMinWaitTime() {
    return this.minWaitTime;
  }

  @Override
  public String toString() {
    return "Schedule [startBlock=" + this.startBlock + ", endBlock=" + this.endBlock + ", startTime=" + this.startTime
        + ", endTime=" + this.endTime + ", minWaitTime=" + this.minWaitTime + "]";
  }

}
