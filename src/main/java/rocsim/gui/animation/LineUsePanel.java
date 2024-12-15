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
package rocsim.gui.animation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import rocsim.gui.model.BlockStatusModel;
import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.gui.widgets.SingleBlockEventPanel;
import rocsim.schedule.model.LineModel;
import rocsim.schedule.model.LineSegmentModel;
import rocsim.schedule.model.TimeModel;

public class LineUsePanel extends JPanel {
  private static final long serialVersionUID = 1L;
  private JComboBox<String> lineCombobox;
  private JPanel usePanel;
  private List<LineModel> rocoLines = new ArrayList<>();
  private StringListDataModel lines = new StringListDataModel();
  private TimeModel timeModel;
  private BlockStatusModel blockStatusModel;
  private List<SingleBlockEventPanel> blockEventPanels = new ArrayList<>();

  public LineUsePanel(BlockStatusModel blockStatusModel, TimeModel timeModel) {
    super();
    this.timeModel = timeModel;
    this.blockStatusModel = blockStatusModel;

    setLayout(new BorderLayout(0, 0));

    JPanel panel = new JPanel();
    add(panel, BorderLayout.NORTH);

    JLabel lblLine = new JLabel("Line");
    panel.add(lblLine);

    this.lineCombobox = new JComboBox<>();
    panel.add(this.lineCombobox);
    this.lineCombobox.setModel(new StringComboBoxModel(this.lines));
    this.lineCombobox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        reorganize((String) LineUsePanel.this.lineCombobox.getSelectedItem());
      }
    });

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane, BorderLayout.CENTER);

    this.usePanel = new JPanel();
    FlowLayout fl_usePanel = (FlowLayout) this.usePanel.getLayout();
    fl_usePanel.setAlignment(FlowLayout.LEFT);
    scrollPane.setViewportView(this.usePanel);
  }

  public void setLines(List<LineModel> rocoLines) {
    this.rocoLines.clear();
    this.rocoLines.addAll(rocoLines);

    List<String> lineNames = new ArrayList<>();
    for (LineModel lineModel : this.rocoLines) {
      lineNames.add(lineModel.getName());
    }
    this.lines.setValueList(lineNames);
    if (!lineNames.isEmpty()) {
      this.lineCombobox.setSelectedIndex(0);
    }
  }

  private void reorganize(String line) {
    LineModel displayLine = null;
    for (LineModel rocoLine : this.rocoLines) {
      if (rocoLine.getName().equals(line)) {
        displayLine = rocoLine;
        break;
      }
    }
    if (displayLine != null) {
      List<SingleBlockEventPanel> newBlockPanel = new ArrayList<>();
      for (LineSegmentModel segment : displayLine.getLineSegments()) {
        newBlockPanel.add(new SingleBlockEventPanel(segment.getWatchBlock(), this.blockStatusModel, this.timeModel));
      }
      this.usePanel.removeAll();
      for (SingleBlockEventPanel blockEventPanel : newBlockPanel) {
        this.usePanel.add(blockEventPanel);
      }
      this.blockEventPanels = newBlockPanel;
    }
    repaint();
    revalidate();
  }

  public void triggerRebuild() {
    for (SingleBlockEventPanel blockEventPanel : this.blockEventPanels) {
      blockEventPanel.triggerRebuild();
    }
  }
}
