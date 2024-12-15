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
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.PlatformModel;
import rocsim.schedule.model.StationModel;

public class StationPanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JTextField stationNameTextField;
  private PlatformFrame platformFrame;

  public StationPanel(EditorContext context) {
    setLayout(new BorderLayout());

    JPanel innerPanel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) innerPanel.getLayout();
    flowLayout.setHgap(10);
    flowLayout.setVgap(2);
    flowLayout.setAlignment(FlowLayout.LEFT);

    JLabel lblName = new JLabel("Station Name");
    innerPanel.add(lblName);

    this.stationNameTextField = new JTextField();
    innerPanel.add(this.stationNameTextField);
    this.stationNameTextField.setColumns(10);

    add(innerPanel, BorderLayout.NORTH);

    this.platformFrame = new PlatformFrame(context);
    add(this.platformFrame, BorderLayout.CENTER);
  }

  public void setModel(StationModel model) {
    this.stationNameTextField.setText(model.getName());
    this.platformFrame.setPlatformModels(model.getPlatforms());
  }

  public StationModel getModel() {
    StationModel model = new StationModel();
    model.setName(this.stationNameTextField.getText());
    for (PlatformModel platformModel : this.platformFrame.getPlatformModels()) {
      model.addPlatform(platformModel);
    }
    return model;
  }

  public void updateContext() {
    this.platformFrame.updateContext();
  }
}
