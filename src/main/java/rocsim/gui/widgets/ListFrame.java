package rocsim.gui.widgets;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rocsim.gui.widgets.ListItemFrame.ListItemListener;

public class ListFrame<T extends DataPanel> extends DataPanel {

  private static final long serialVersionUID = 1L;
  private List<ListItemFrame<T>> items = new ArrayList<>();
  private ListItemFactory<T> factory;
  private Box contentPanel;

  public interface ListItemFactory<Z extends JPanel> {
    Z createNewItem();
  };

  private ListItemListener<T> itemListener = new ListItemListener<T>() {

    @Override
    public void addItem(ListItemFrame<T> frame) {
      T newItem = ListFrame.this.factory.createNewItem();
      ListItemFrame<T> newFrame = new ListItemFrame<>();
      newFrame.setEmbedded(newItem);
      newFrame.addListener(ListFrame.this.itemListener);
      newFrame.addDataListener(ListFrame.this.dataListener);
      int index = ListFrame.this.items.indexOf(frame);
      if (index > 0) {
        ListFrame.this.items.add(index + 1, newFrame);
      } else {
        ListFrame.this.items.add(newFrame);
      }
      rebuildChilds();
    }

    @Override
    public void removeItem(ListItemFrame<T> frame) {
      if (ListFrame.this.items.size() > 1 && ListFrame.this.items.remove(frame)) {
        rebuildChilds();
      }
    }
  };

  private ListDataListener dataListener = new ListDataListener() {

    @Override
    public void intervalRemoved(ListDataEvent arg0) {
      contentsChanged(arg0);
    }

    @Override
    public void intervalAdded(ListDataEvent arg0) {
      contentsChanged(arg0);

    }

    @Override
    public void contentsChanged(ListDataEvent arg0) {
      fireDataChanged();
    }
  };

  private ListFrame() {
    super();
    this.setLayout(new BorderLayout());
    this.contentPanel = Box.createVerticalBox();
    add(this.contentPanel, BorderLayout.NORTH);
  }

  public ListFrame(ListItemFactory<T> factory) {
    this();
    this.factory = factory;
    T newItem = factory.createNewItem();
    ListItemFrame<T> newFrame = new ListItemFrame<>();
    newFrame.addListener(ListFrame.this.itemListener);
    newFrame.addDataListener(this.dataListener);
    newFrame.setEmbedded(newItem);
    ListFrame.this.items.add(newFrame);
    rebuildChilds();
  }

  public void setContent(List<T> newItems) {
    this.items.clear();
    if (newItems.isEmpty()) {
      T newItem = this.factory.createNewItem();
      ListItemFrame<T> newFrame = new ListItemFrame<>();
      newFrame.addListener(ListFrame.this.itemListener);
      newFrame.addDataListener(this.dataListener);
      newFrame.setEmbedded(newItem);
      this.items.add(newFrame);
    } else {
      for (T newItem : newItems) {
        ListItemFrame<T> newFrame = new ListItemFrame<>();
        newFrame.addListener(ListFrame.this.itemListener);
        newFrame.addDataListener(this.dataListener);
        newFrame.setEmbedded(newItem);
        this.items.add(newFrame);
      }
    }
    rebuildChilds();
  }

  public List<T> getContent() {
    List<T> result = new ArrayList<>();
    for (ListItemFrame<T> listItemFrame : this.items) {
      result.add(listItemFrame.getEmbedded());
    }
    return result;
  }

  private void rebuildChilds() {
    this.contentPanel.removeAll();
    for (ListItemFrame<T> listItemFrame : ListFrame.this.items) {
      this.contentPanel.add(listItemFrame);
    }
    this.contentPanel.add(Box.createGlue());
  }
}