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
package rocsim.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import rocsim.gui.animation.AnimationContainer;
import rocsim.gui.editor.EditorContainer;
import rocsim.schedule.model.TimeModel;
import rocsim.xml.ReadPlan;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private JTabbedPane tabbedPane;
  private EditorContainer editorContainer;
  private AnimationContainer animationContainer;
  private TimeModel timeModel = new TimeModel();

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));

    this.tabbedPane = new JTabbedPane();

    this.editorContainer = new EditorContainer(planner, this.timeModel);
    this.animationContainer = new AnimationContainer(planner, this.timeModel);

    this.tabbedPane.addTab("Animation", this.animationContainer.getPlanPannel());
    this.tabbedPane.addTab("Blocks", this.animationContainer.getBlockUsePanel());
    this.tabbedPane.addTab("Logs", this.animationContainer.getLogPanel());
    this.tabbedPane.addTab("Track", this.editorContainer.getEditorPanel());
    this.tabbedPane.addTab("Locos", this.editorContainer.getLocoFrame());
    this.tabbedPane.addTab("Trips", this.editorContainer.getTripFrame());

    getContentPane().add(this.tabbedPane, BorderLayout.CENTER);

    getContentPane().add(this.animationContainer.getControlPanel(), BorderLayout.SOUTH);
    pack();

  }

  @Override
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  public void jMenuFileExit_actionPerformed(ActionEvent e) {

    System.exit(0);
  }

}
