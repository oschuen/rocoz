/*
 * Copyright © 2024 Oliver Schünemann (oschuen@users.noreply.github.com)
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
