package rocsim.gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.UIManager;

public class SimApp {
  boolean packFrame = true;

  public SimApp() {
    MainFrame frame = new MainFrame();
    if (this.packFrame) {
      frame.pack();
    } else {
      frame.validate();
    }
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }

  /** Main-Methode */
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
      e.printStackTrace();
    }
    new SimApp();
  }
}
