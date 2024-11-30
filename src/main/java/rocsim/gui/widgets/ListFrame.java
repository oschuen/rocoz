package rocsim.gui.widgets;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

import rocsim.gui.widgets.ListItemFrame.ListItemListener;

public class ListFrame<T extends JPanel> extends JPanel {

  private static final long serialVersionUID = 1L;
  private List<ListItemFrame<T>> items = new ArrayList<>();
  private ListItemFactory<T> factory;
  private ListItemFrame<?> topPanel;
  // private JPanel contentPanel;
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

  private ListFrame() {
    super();
    this.setLayout(new BorderLayout());
    this.contentPanel = Box.createVerticalBox();
    add(this.contentPanel, BorderLayout.NORTH);
  }

  public ListFrame(ListItemFactory<T> factory) {
    this();
    this.factory = factory;
    this.topPanel = null;
    T newItem = factory.createNewItem();
    ListItemFrame<T> newFrame = new ListItemFrame<>();
    newFrame.addListener(ListFrame.this.itemListener);
    newFrame.setEmbedded(newItem);
    ListFrame.this.items.add(newFrame);
    rebuildChilds();
  }

  public <Z extends JPanel> ListFrame(ListItemFrame<Z> topPanel, ListItemFactory<T> factory) {
    this();
    this.factory = factory;
    this.topPanel = topPanel;
    rebuildChilds();
    topPanel.addListener(new ListItemListener<Z>() {

      @Override
      public void addItem(ListItemFrame<Z> frame) {
        T newItem = factory.createNewItem();
        ListItemFrame<T> newFrame = new ListItemFrame<>();
        newFrame.addListener(ListFrame.this.itemListener);
        newFrame.setEmbedded(newItem);
        ListFrame.this.items.add(newFrame);
        rebuildChilds();
      }

      @Override
      public void removeItem(ListItemFrame<Z> frame) {
        if (ListFrame.this.items.remove(frame)) {
          rebuildChilds();
        }
      }
    });
  }

  private void rebuildChilds() {
    this.contentPanel.removeAll();
    if (this.topPanel != null) {
      this.contentPanel.add(this.topPanel);
    }
    for (ListItemFrame<T> listItemFrame : ListFrame.this.items) {
      this.contentPanel.add(listItemFrame);
    }
    this.contentPanel.add(Box.createGlue());
  }
}
