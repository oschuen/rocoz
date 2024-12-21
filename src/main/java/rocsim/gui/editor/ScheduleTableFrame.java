package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MaskFormatter;

import rocsim.gui.model.StringComboBoxModel;
import rocsim.gui.model.StringListDataModel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TimeModel.TimeModelChangeListener;
import rocsim.track.Block;

public class ScheduleTableFrame extends JPanel {
  private static final long serialVersionUID = 1L;
  private JTable table;
  private EditorContext context;
  private StringListDataModel stationListModel;
  private String firstBlock = "";

  public ScheduleTableFrame(EditorContext context) {
    this.context = context;
    setLayout(new BorderLayout(5, 0));

    JPanel panel = new JPanel();
    add(panel, BorderLayout.EAST);

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
    gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
    gridBagLayout.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
    gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
    panel.setLayout(gridBagLayout);

    JButton btnAdd = new JButton("Add Before");
    GridBagConstraints gbc_btnAdd = new GridBagConstraints();
    gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
    gbc_btnAdd.gridx = 1;
    gbc_btnAdd.gridy = 1;
    gbc_btnAdd.fill = GridBagConstraints.HORIZONTAL;
    panel.add(btnAdd, gbc_btnAdd);
    btnAdd.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        addRow(false);
      }
    });

    JButton btnAddAfter = new JButton("Add After");
    GridBagConstraints gbc_btnAddAfter = new GridBagConstraints();
    gbc_btnAddAfter.insets = new Insets(0, 0, 5, 0);
    gbc_btnAddAfter.gridx = 1;
    gbc_btnAddAfter.gridy = 3;
    gbc_btnAddAfter.fill = GridBagConstraints.HORIZONTAL;
    panel.add(btnAddAfter, gbc_btnAddAfter);
    btnAddAfter.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        addRow(true);
      }
    });

    JButton btnCalculate = new JButton("Calculate");
    GridBagConstraints gbc_btnCalculate = new GridBagConstraints();
    gbc_btnCalculate.insets = new Insets(0, 0, 5, 0);
    gbc_btnCalculate.gridx = 1;
    gbc_btnCalculate.gridy = 5;
    gbc_btnCalculate.fill = GridBagConstraints.HORIZONTAL;
    panel.add(btnCalculate, gbc_btnCalculate);
    btnCalculate.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        calculateDuration();
      }
    });

    JButton btnRemove = new JButton("Remove");
    GridBagConstraints gbc_btnRemove = new GridBagConstraints();
    gbc_btnRemove.insets = new Insets(0, 0, 5, 0);
    gbc_btnRemove.gridx = 1;
    gbc_btnRemove.gridy = 7;
    gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
    panel.add(btnRemove, gbc_btnRemove);

    btnRemove.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        removeRow();
      }
    });

    this.stationListModel = new StringListDataModel();
    this.stationListModel.setValueList(context.getStationNames());

    this.table = new JTable();
    ((DefaultTableModel) this.table.getModel()).addColumn("Station");
    ((DefaultTableModel) this.table.getModel()).addColumn("Platform");
    ((DefaultTableModel) this.table.getModel()).addColumn("Duration");
    ((DefaultTableModel) this.table.getModel()).addColumn("Pause");
    ((DefaultTableModel) this.table.getModel()).addColumn("Comment");
    this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.table.setRowSelectionAllowed(true);
    this.table.setCellSelectionEnabled(true);
    this.table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    add(this.table, BorderLayout.CENTER);
    TableColumnModel colModel = this.table.getColumnModel();
    colModel.getColumn(0).setCellEditor(new MyStationEditor());
    colModel.getColumn(1).setCellEditor(new MyPlatformEditor());
    colModel.getColumn(2).setCellRenderer(new MyDurationEditor());
    colModel.getColumn(2).setCellEditor(new MyDurationEditor());
    colModel.getColumn(3).setCellRenderer(new MyDurationEditor());
    colModel.getColumn(3).setCellEditor(new MyDurationEditor());

    colModel.getColumn(4).setPreferredWidth(500);

    add(this.table.getTableHeader(), BorderLayout.NORTH);
    this.table.setRowHeight(20);
    context.getTimeModel().addListener(new TimeModelChangeListener() {
      @Override
      public void timeModelChanged() {
        ScheduleTableFrame.this.table.repaint();
      }
    });
  }

  private void removeRow() {
    int row = this.table.getSelectedRow();
    if (row >= 0 && !this.table.isEditing()) {
      ((DefaultTableModel) this.table.getModel()).removeRow(row);
    }
  }

  private void addRow(boolean after) {
    int row = this.table.getSelectedRow();
    Object[] data = new Object[] { "", "", Integer.valueOf(60), Integer.valueOf(0), "" };
    if (row >= 0) {
      if (after) {
        ((DefaultTableModel) this.table.getModel()).insertRow(row + 1, data);
      } else {
        ((DefaultTableModel) this.table.getModel()).insertRow(row, data);
      }
    }
    if (this.table.getRowCount() == 0) {
      ((DefaultTableModel) this.table.getModel()).insertRow(0, data);
    }
  }

  private void calculateDuration() {
    int row = this.table.getSelectedRow();
    if (row >= 0) {
      String firstBlock;
      if (row > 0) {
        String formerStation = (String) this.table.getValueAt(row - 1, 0);
        String formerPlatform = (String) this.table.getValueAt(row - 1, 1);
        firstBlock = this.context.getBlockForPlatform(formerStation, formerPlatform);
      } else {
        firstBlock = this.firstBlock;
      }
      String currentStation = (String) this.table.getValueAt(row, 0);
      String currentPlatform = (String) this.table.getValueAt(row, 1);
      String currentBlock = this.context.getBlockForPlatform(currentStation, currentPlatform);
      Block block = this.context.getBlock(firstBlock, currentBlock);
      int duration = block.getDriveTime(60);
      this.table.setValueAt(Integer.valueOf(duration), row, 2);
    }
  }

  public void setScheduleModels(List<ScheduleModel> models) {
    ((DefaultTableModel) this.table.getModel()).setNumRows(models.size());
    for (int i = 0; i < models.size(); i++) {
      ScheduleModel model = models.get(i);
      Entry<String, String> start = this.context.getStationAndPlatform(model.getEndBlock());
      if (start != null) {
        this.table.setValueAt(start.getKey(), i, 0);
        this.table.setValueAt(start.getValue(), i, 1);
        this.table.setValueAt(Integer.valueOf(model.getDuration()), i, 2);
        this.table.setValueAt(Integer.valueOf(model.getPause()), i, 3);
        this.table.setValueAt(model.getComment(), i, 4);
      }
    }
  }

  public List<ScheduleModel> getScheduleModels() {
    List<ScheduleModel> models = new ArrayList<>();
    String blockK1 = this.firstBlock;
    for (int i = 0; i < this.table.getRowCount(); ++i) {
      ScheduleModel model = new ScheduleModel();
      model.setStartBlock(blockK1);
      String currentStation = (String) this.table.getValueAt(i, 0);
      String currentPlatform = (String) this.table.getValueAt(i, 1);
      String currentBlock = this.context.getBlockForPlatform(currentStation, currentPlatform);
      model.setEndBlock(currentBlock);
      model.setDuration(((Integer) this.table.getValueAt(i, 2)).intValue());
      model.setPause(((Integer) this.table.getValueAt(i, 3)).intValue());
      model.setComment((String) this.table.getValueAt(i, 4));
      blockK1 = currentBlock;
      models.add(model);
    }
    return models;
  }

  public void updateContext() {
    this.stationListModel.setValueList(this.context.getStationNames());
  }

  public void updatePreviousBlockIds(String blockId) {
    this.firstBlock = blockId;
  }

  private class MyStationEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> comboBox;

    public MyStationEditor() {
      this.comboBox = new JComboBox<>();
      this.comboBox.setModel(new StringComboBoxModel(ScheduleTableFrame.this.stationListModel));
    }

    @Override
    public Object getCellEditorValue() {
      return this.comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
      this.comboBox.setSelectedItem(arg1);
      return this.comboBox;
    }
  }

  private class MyPlatformEditor extends AbstractCellEditor implements TableCellEditor {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> comboBox;
    private StringListDataModel platforms = new StringListDataModel();

    public MyPlatformEditor() {
      this.comboBox = new JComboBox<>();
      this.comboBox.setModel(new StringComboBoxModel(this.platforms));
    }

    @Override
    public Object getCellEditorValue() {
      return this.comboBox.getSelectedItem();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      String station = (String) table.getValueAt(row, 0);
      this.platforms.setValueList(ScheduleTableFrame.this.context.getPlatforms(station));
      this.comboBox.setSelectedItem(value);
      return this.comboBox;
    }
  }

  private class MyDurationEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    private static final long serialVersionUID = 1L;
    private JFormattedTextField durationTextField;
    private boolean displayRealTime = false;

    public MyDurationEditor() {
      MaskFormatter mask = null;
      try {
        mask = new MaskFormatter("##:##:##");
        mask.setPlaceholderCharacter('#');
      } catch (ParseException e) {
      }
      this.durationTextField = new JFormattedTextField(mask);
    }

    @Override
    public Object getCellEditorValue() {
      int pauseTime = ScheduleTableFrame.this.context.getTimeModel()
          .convertTimeString(this.durationTextField.getText());
      if (this.displayRealTime) {
        return Integer.valueOf(pauseTime);
      }
      return Integer.valueOf(pauseTime / ScheduleTableFrame.this.context.getTimeModel().getRadix());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      this.displayRealTime = ScheduleTableFrame.this.context.getTimeModel().isDisplayRealTime();
      int timeValue;
      if (value == null) {
        timeValue = 60;
      } else {
        timeValue = ((Integer) value).intValue();
      }
      if (!this.displayRealTime) {
        timeValue *= ScheduleTableFrame.this.context.getTimeModel().getRadix();
      }
      this.durationTextField.setValue(ScheduleTableFrame.this.context.getTimeModel().getTimeSecString(timeValue));
      return this.durationTextField;
    }

    @Override
    public boolean stopCellEditing() {
      try {
        this.durationTextField.commitEdit();
      } catch (ParseException e) {
        return false;
      }
      return super.stopCellEditing();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {
      return getTableCellEditorComponent(table, value, isSelected, row, column);
    }
  }

  public void addDataListener(ListDataListener dataListener) {

  }
}
