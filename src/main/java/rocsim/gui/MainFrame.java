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
package rocsim.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import rocsim.gui.animation.AnimationContainer;
import rocsim.gui.editor.EditorContainer;
import rocsim.schedule.model.TimeModel;

public class MainFrame extends JFrame {

  private static final long serialVersionUID = 1L;
  private JTabbedPane tabbedPane;
  private EditorContainer editorContainer;
  private AnimationContainer animationContainer;
  private TimeModel timeModel = new TimeModel();
  private File currentFile = new File(".");
  private File activeFile = null;
  private Preferences prefs = Preferences.userRoot().node(MainFrame.class.getName());
  private static final String CURRENT_FILE = "Current File";
  private JsonObject lastConfig;

  public MainFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    this.currentFile = new File(this.prefs.get(CURRENT_FILE, "."));
    this.tabbedPane = new JTabbedPane();

    this.editorContainer = new EditorContainer(this.timeModel);
    this.animationContainer = new AnimationContainer(this.timeModel);

    try (JsonReader reader = Json.createReader(MainFrame.class.getResourceAsStream("default.rcz"))) {
      this.lastConfig = reader.readObject();
      this.editorContainer.fromJson(this.lastConfig);
      this.animationContainer.fromJson(this.lastConfig);
    } catch (Exception exp) {
      exp.printStackTrace();
    }

    this.tabbedPane.addTab("Animation", this.animationContainer.getPlanPannel());
    this.tabbedPane.addTab("Blocks", this.animationContainer.getLineUsePanel());
    this.tabbedPane.addTab("Logs", this.animationContainer.getLogPanel());
    this.tabbedPane.addTab("Track", this.editorContainer.getEditorPanel());
    this.tabbedPane.addTab("Locos", this.editorContainer.getLocoFrame());
    this.tabbedPane.addTab("Stations", this.editorContainer.getStationFrame());
    this.tabbedPane.addTab("Lines", this.editorContainer.getLineFrame());
    this.tabbedPane.addTab("Trips", this.editorContainer.getTripFrame());
    this.tabbedPane.addTab("Timetable", this.editorContainer.getTimeTableFrame());
    this.tabbedPane.addTab("StationData", this.editorContainer.getStationData());

//    System.out.println("Plan Pannel " + this.animationContainer.getPlanPannel().getPreferredSize());
//    System.out.println("Animation Pannel " + this.animationContainer.getLineUsePanel().getPreferredSize());
//    System.out.println("Log Pannel " + this.animationContainer.getLogPanel().getPreferredSize());
//    System.out.println("time Pannel " + this.editorContainer.getTimeTableFrame().getPreferredSize());

    this.tabbedPane.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent arg0) {
        MainFrame.this.editorContainer.frameSelected(MainFrame.this.tabbedPane.getSelectedComponent());
      }
    });

    getContentPane().add(this.tabbedPane, BorderLayout.CENTER);

    getContentPane().add(this.animationContainer.getControlPanel(), BorderLayout.SOUTH);

    JMenuBar menuBar = new JMenuBar();
    getContentPane().add(menuBar, BorderLayout.NORTH);

    JMenu mnFile = new JMenu("File");
    menuBar.add(mnFile);

    JMenuItem mntmLoad = new JMenuItem("Load");
    mnFile.add(mntmLoad);
    mntmLoad.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        jMenuFileLoad_actionPerformed(arg0);
      }
    });

    JMenuItem mntmSave = new JMenuItem("Save");
    mnFile.add(mntmSave);
    mntmSave.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jMenuFileSave_actionPerformed(arg0);
      }
    });

    JMenuItem mntmSaveAs = new JMenuItem("Save As");
    mnFile.add(mntmSaveAs);
    mntmSaveAs.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jMenuFileSaveAs_actionPerformed(arg0);
      }
    });

    JMenuItem mntmExit = new JMenuItem("Exit");
    mnFile.add(mntmExit);
    mntmExit.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jMenuFileExit_actionPerformed(arg0);
      }
    });

    JMenu mnEdit = new JMenu("Edit");
    menuBar.add(mnEdit);

    JMenuItem mntmApply = new JMenuItem("Apply");
    mnEdit.add(mntmApply);
    mntmApply.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        jMenuEditApply_actionPerformed(arg0);
      }
    });
    pack();

  }

  public void jMenuFileLoad_actionPerformed(ActionEvent e) {
    JFileChooser choose = null;
    FileFilter filter = new FileNameExtensionFilter("Sim File (*.rcz)", "rcz");

    if (null == this.currentFile) {
      choose = new JFileChooser();
      choose.addChoosableFileFilter(filter);
    } else {
      choose = new JFileChooser(this.currentFile);
      choose.addChoosableFileFilter(filter);
    }
    choose.setAcceptAllFileFilterUsed(true);

    int returnVal = choose.showOpenDialog(this);
    File f = choose.getSelectedFile();
    if ((f != null) && (returnVal == JFileChooser.APPROVE_OPTION)) {
      this.currentFile = f;
      this.activeFile = f;
      try (JsonReader reader = Json.createReader(new FileReader(f))) {
        this.lastConfig = reader.readObject();
        this.animationContainer.fromJson(this.lastConfig);
        this.editorContainer.fromJson(this.lastConfig);
      } catch (Exception exp) {
        exp.printStackTrace();
      }
    }
    javax.swing.SwingUtilities.invokeLater(() -> {
      this.repaint();
    });
  }

  private void createBackup(File f) {
    String extension = FilenameUtils.getExtension(f.getAbsolutePath());
    String name = FilenameUtils.getBaseName(f.getAbsolutePath());
    String path = FilenameUtils.getFullPath(f.getAbsolutePath());
    String backup = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
    File backupFile = new File(path + File.separator + name + "_" + backup + "." + extension);
    try {
      FileUtils.copyFile(f, backupFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public int jMenuFileSave_actionPerformed(ActionEvent e) {
    if (this.activeFile == null) {
      return jMenuFileSaveAs_actionPerformed(e);
    } else {
      if (this.activeFile.exists()) {
        createBackup(this.activeFile);
      }
      Map<String, Object> config = new HashMap<>(1);
      config.put(JsonGenerator.PRETTY_PRINTING, true);
      JsonWriterFactory factory = Json.createWriterFactory(config);
      try (JsonWriter jsonWriter = factory.createWriter(new FileWriter(this.activeFile))) {
        this.lastConfig = this.editorContainer.toJson();
        jsonWriter.write(this.lastConfig);
      } catch (Exception exp) {
        exp.printStackTrace();
      }
    }
    return JFileChooser.APPROVE_OPTION;
  }

  public int jMenuFileSaveAs_actionPerformed(ActionEvent e) {
    int returnVal = JFileChooser.CANCEL_OPTION;
    try {
      JFileChooser choose;
      FileFilter filter = new FileNameExtensionFilter("Sim File (*.rcz)", "rcz");
      if (null == this.currentFile) {
        choose = new JFileChooser();
        choose.addChoosableFileFilter(filter);
      } else {
        choose = new JFileChooser(this.currentFile);
        choose.addChoosableFileFilter(filter);
      }
      choose.setAcceptAllFileFilterUsed(true);
      returnVal = choose.showSaveDialog(this);
      File f = choose.getSelectedFile();
      if ((f != null) && (returnVal == JFileChooser.APPROVE_OPTION)) {
        this.currentFile = f;
        this.activeFile = f;
        if (f.exists()) {
          createBackup(f);
        }
        Map<String, Object> config = new HashMap<>(1);
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory factory = Json.createWriterFactory(config);
        try (JsonWriter jsonWriter = factory.createWriter(new FileWriter(f))) {
          this.lastConfig = this.editorContainer.toJson();
          jsonWriter.write(this.lastConfig);
        } catch (Exception exp) {
          throw exp;
        }
      }
    } catch (Exception exp) {
      exp.printStackTrace();
    }
    return returnVal;
  }

  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    if (this.currentFile != null) {
      this.prefs.put(CURRENT_FILE, this.currentFile.getAbsolutePath());
    }
    if (!this.lastConfig.equals(this.editorContainer.toJson())) {
      int option = JOptionPane.showConfirmDialog(null, "Store Changes", "Unsaved Changes",
          JOptionPane.YES_NO_CANCEL_OPTION);
      if (option == 0) {
        if (jMenuFileSave_actionPerformed(null) == JFileChooser.CANCEL_OPTION) {
          setVisible(true);
          return;
        }
      } else if (option == 2) {
        setVisible(true);
        return;
      }
    }
    System.exit(0);
  }

  public void jMenuEditApply_actionPerformed(ActionEvent e) {
    this.animationContainer.fromJson(this.editorContainer.toJson());
  }

  @Override
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }
}
