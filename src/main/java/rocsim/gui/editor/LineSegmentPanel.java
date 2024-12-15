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

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.DataPanel;
import rocsim.schedule.model.LineSegmentModel;

public class LineSegmentPanel extends DataPanel {
  private static final long serialVersionUID = 1L;
  private JComboBox<String> stationComboBox;
  private JComboBox<String> watchBlockComboBox;
  private EditorContext context;
  private StringListDataModel watchBlockIdDataModel;
  private StringListDataModel stationNameDataModel;

  public LineSegmentPanel(EditorContext context) {
    this.context = context;
    this.watchBlockIdDataModel = new StringListDataModel();
    this.watchBlockIdDataModel.setValueList(context.getWatchBlockIds());

    FlowLayout flowLayout = (FlowLayout) getLayout();
    flowLayout.setHgap(10);
    flowLayout.setAlignment(FlowLayout.LEFT);

    JLabel lblNewLabel = new JLabel("WatchBlock");
    add(lblNewLabel);

    this.watchBlockComboBox = new JComboBox<>();
    add(this.watchBlockComboBox);
    this.watchBlockComboBox.setModel(new StringComboBoxModel(this.watchBlockIdDataModel));

    JLabel lblStation_1 = new JLabel("Station");
    add(lblStation_1);

    this.stationComboBox = new JComboBox<>();
    add(this.stationComboBox);
    this.stationNameDataModel = new StringListDataModel();
    this.stationNameDataModel.setValueList(context.getStationNames());
    this.stationComboBox.setModel(new StringComboBoxModel(this.stationNameDataModel));

  }

  public void setModel(LineSegmentModel model) {
    this.stationComboBox.setSelectedItem(model.getStationTwo());
    this.watchBlockComboBox.setSelectedItem(model.getWatchBlock());
  }

  public LineSegmentModel getModel() {
    LineSegmentModel model = new LineSegmentModel();
    model.setStationTwo((String) this.stationComboBox.getSelectedItem());
    model.setWatchBlock((String) this.watchBlockComboBox.getSelectedItem());
    return model;
  }

  public void updateContext() {
    this.watchBlockIdDataModel.setValueList(this.context.getWatchBlockIds());
    this.stationNameDataModel.setValueList(this.context.getStationNames());
  }

}
