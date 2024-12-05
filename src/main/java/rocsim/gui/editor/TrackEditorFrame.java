package rocsim.gui.editor;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import rocsim.gui.widgets.TileSelectionPanel;

public class TrackEditorFrame extends JPanel {
  private static final long serialVersionUID = 1L;
  private TrackEditorPanel planPannel;
  private TileSelectionPanel panel;

  public TrackEditorFrame() {
    setLayout(new BorderLayout(0, 0));

    this.panel = new TileSelectionPanel();
    this.planPannel = new TrackEditorPanel(this.panel);
    add(this.panel, BorderLayout.NORTH);
    add(this.planPannel, BorderLayout.CENTER);
  }

}
