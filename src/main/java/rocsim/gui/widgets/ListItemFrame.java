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
package rocsim.gui.widgets;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class ListItemFrame<T extends DataPanel> extends DataPanel {
  private static final long serialVersionUID = 1L;
  private PlusButton plusButton;
  private MinusButton minusButton;
  private List<ListItemListener<T>> listeners = new ArrayList<>();
  private T embedded = null;
  private JPanel panel;
  private JPanel buttonPanel;

  public interface ListItemListener<Z extends DataPanel> {
    void addItem(ListItemFrame<Z> frame);

    void removeItem(ListItemFrame<Z> frame);
  }

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

  public ListItemFrame() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);

    this.panel = new JPanel();
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.NORTH;
    gbc.insets = new Insets(0, 0, 0, 0);
    gbc.gridx = 11;
    gbc.gridy = 0;

    add(this.panel, gbc);

    this.buttonPanel = new JPanel();
    this.buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    JButton okButton = new JButton("OK");
    this.buttonPanel.add(okButton);
    okButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        notifyRemove();
      }
    });

    JButton cancelButton = new JButton("CANCEL");
    this.buttonPanel.add(cancelButton);
    cancelButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        javax.swing.SwingUtilities.invokeLater(() -> {
          remove(ListItemFrame.this.buttonPanel);
          setEmbedded(ListItemFrame.this.embedded);
          repaint();
          revalidate();
        });

      }
    });

    this.plusButton = new PlusButton();
    this.panel.add(this.plusButton);
    this.plusButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        notifyAdd();
      }
    });

    this.minusButton = new MinusButton();
    this.panel.add(this.minusButton);
    this.minusButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        javax.swing.SwingUtilities.invokeLater(() -> {
          if (ListItemFrame.this.embedded != null) {
            remove(ListItemFrame.this.embedded);
          }
          GridBagConstraints gbc2 = new GridBagConstraints();
          gbc2.fill = GridBagConstraints.HORIZONTAL;
          gbc2.anchor = GridBagConstraints.WEST;
          gbc2.insets = new Insets(0, 0, 0, 0);
          gbc2.gridx = 0;
          gbc2.gridy = 0;
          gbc2.weightx = 10;

          add(ListItemFrame.this.buttonPanel, gbc2);
          repaint();
          revalidate();
        });
      }
    });
  }

  private void notifyAdd() {
    for (ListItemListener<T> listItemListener : this.listeners) {
      listItemListener.addItem(this);
    }
  }

  private void notifyRemove() {
    for (ListItemListener<T> listItemListener : this.listeners) {
      listItemListener.removeItem(this);
    }
  }

  public void addListener(ListItemListener<T> listener) {
    this.listeners.add(listener);
  }

  /**
   * @return the embedded
   */
  public T getEmbedded() {
    return this.embedded;
  }

  /**
   * @param embedded the embedded to set
   */
  public void setEmbedded(T embedded) {
    if (this.embedded != null) {
      remove(this.embedded);
      this.embedded.removeDataListener(this.dataListener);
    }
    this.embedded = embedded;
    embedded.addDataListener(this.dataListener);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(0, 0, 0, 0);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 10;
    gbc.weightx = 100;

    add(embedded, gbc);
  }
}
