package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import rocsim.schedule.model.TripModel;

public class ConfigTripDialog extends JDialog {

  private TripStationPanel tripPanel;

  public ConfigTripDialog(EditorContext context, String tripId, boolean newTrip) {
    setTitle(tripId);
    setModal(true);

    this.tripPanel = new TripStationPanel(context);
    TripModel tripModel = context.getTripModel(tripId);
    this.tripPanel.setModel(tripModel);
    getContentPane().add(this.tripPanel, BorderLayout.CENTER);

    JPanel panel = new JPanel();
    getContentPane().add(panel, BorderLayout.SOUTH);

    JButton btnNewButton = new JButton("OK");
    btnNewButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        TripModel updatedModel = ConfigTripDialog.this.tripPanel.getModel();
        if (newTrip) {
          context.addModel(updatedModel);
        } else {
          context.updateModel(getTitle(), updatedModel);
        }
        setVisible(false);
      }
    });
    panel.add(btnNewButton);
    pack();
  }

  private static final long serialVersionUID = 1L;

}
