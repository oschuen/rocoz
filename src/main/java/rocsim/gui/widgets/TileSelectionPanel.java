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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import rocsim.gui.model.TileEditModel;
import rocsim.gui.tiles.Block;
import rocsim.gui.tiles.Curve;
import rocsim.gui.tiles.LeftSwitch;
import rocsim.gui.tiles.RightSwitch;
import rocsim.gui.tiles.Tile;
import rocsim.gui.tiles.Track;
import rocsim.gui.widgets.TileButton.TileFactory;
import rocsim.schedule.model.TrackPlanModel.BlockKind;
import rocsim.schedule.model.TrackPlanModel.Direction;

public class TileSelectionPanel extends JScrollPane implements TileEditModel {
  private static final long serialVersionUID = 1L;
  private List<TileButton> buttons = new ArrayList<>();
  private JPanel panel;
  private TileDeleteButton tileDeleteButton;
  private UndoButton undoButton;
  private TileSelectionMoveButton tileSelectionMoveButton;
  private TileSelectionDropButton tileSelectionDropButton;
  private List<TileModelListener> undoListeners = new ArrayList<>();

  private TileButton westTrackButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      return new Track("", 0, 0, 0.5F, Direction.WEST);
    }
  });

  private TileButton northTrackButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      return new Track("", 0, 0, 0.5F, Direction.NORTH);
    }
  });

  private int blockCounter = -1;
  private int stellBlockCounter = -1;
  private int watchBlockCounter = -1;
  private TileButton westBlockButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      Block block = new Block("", 0, 0, 0.5F, Direction.WEST, BlockKind.BLOCK);
      block.setId("bl" + TileSelectionPanel.this.blockCounter);
      TileSelectionPanel.this.blockCounter++;
      return block;
    }
  });

  private TileButton northBlockButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      Block block = new Block("", 0, 0, 0.5F, Direction.NORTH, BlockKind.BLOCK);
      block.setId("bl" + TileSelectionPanel.this.blockCounter);
      TileSelectionPanel.this.blockCounter++;
      return block;
    }
  });

  private TileButton westStellBlockButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      Block block = new Block("", 0, 0, 0.5F, Direction.WEST, BlockKind.STELLBLOCK);
      block.setId("sb" + TileSelectionPanel.this.stellBlockCounter);
      TileSelectionPanel.this.stellBlockCounter++;
      return block;
    }
  });

  private TileButton northStellBlockButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      Block block = new Block("", 0, 0, 0.5F, Direction.NORTH, BlockKind.STELLBLOCK);
      block.setId("sb" + TileSelectionPanel.this.stellBlockCounter);
      TileSelectionPanel.this.stellBlockCounter++;
      return block;
    }
  });

  private TileButton westWatchBlockButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      Block block = new Block("", 0, 0, 0.5F, Direction.WEST, BlockKind.WATCHBLOCK);
      block.setId("wb" + TileSelectionPanel.this.watchBlockCounter);
      TileSelectionPanel.this.watchBlockCounter++;
      return block;
    }
  });

  private TileButton northWatchBlockButton = new TileButton(new TileFactory() {

    @Override
    public Tile createTile() {
      Block block = new Block("", 0, 0, 0.5F, Direction.NORTH, BlockKind.WATCHBLOCK);
      block.setId("wb" + TileSelectionPanel.this.watchBlockCounter);
      TileSelectionPanel.this.watchBlockCounter++;
      return block;
    }
  });

  private class CurveFactory implements TileFactory {
    private Direction dir;

    public CurveFactory(Direction dir) {
      this.dir = dir;
    }

    @Override
    public Tile createTile() {
      return new Curve("", 0, 0, 0.5F, this.dir);
    }
  };

  private TileButton northCurve = new TileButton(new CurveFactory(Direction.NORTH));
  private TileButton westCurve = new TileButton(new CurveFactory(Direction.WEST));
  private TileButton southCurve = new TileButton(new CurveFactory(Direction.SOUTH));
  private TileButton eastCurve = new TileButton(new CurveFactory(Direction.EAST));

  private class LeftSwitchFactory implements TileFactory {
    private Direction dir;

    public LeftSwitchFactory(Direction dir) {
      this.dir = dir;
    }

    @Override
    public Tile createTile() {
      return new LeftSwitch("", 0, 0, 0.5F, this.dir);
    }
  };

  private TileButton northLeftSwitch = new TileButton(new LeftSwitchFactory(Direction.NORTH));
  private TileButton westLeftSwitch = new TileButton(new LeftSwitchFactory(Direction.WEST));
  private TileButton southLeftSwitch = new TileButton(new LeftSwitchFactory(Direction.SOUTH));
  private TileButton eastLeftSwitch = new TileButton(new LeftSwitchFactory(Direction.EAST));

  private class RightSwitchFactory implements TileFactory {
    private Direction dir;

    public RightSwitchFactory(Direction dir) {
      this.dir = dir;
    }

    @Override
    public Tile createTile() {
      return new RightSwitch("", 0, 0, 0.5F, this.dir);
    }
  };

  private TileButton northRightSwitch = new TileButton(new RightSwitchFactory(Direction.NORTH));
  private TileButton westRightSwitch = new TileButton(new RightSwitchFactory(Direction.WEST));
  private TileButton southRightSwitch = new TileButton(new RightSwitchFactory(Direction.SOUTH));
  private TileButton eastRightSwitch = new TileButton(new RightSwitchFactory(Direction.EAST));

  public TileSelectionPanel() {
    setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
    setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    this.panel = new JPanel();
    FlowLayout flowLayout = new FlowLayout();
    flowLayout.setAlignment(FlowLayout.LEFT);
    this.panel.setLayout(flowLayout);
    setViewportView(this.panel);
    this.tileDeleteButton = new TileDeleteButton();
    this.tileDeleteButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        TileSelectionPanel.this.tileDeleteButton.setChecked(!TileSelectionPanel.this.tileDeleteButton.isChecked());
        TileSelectionPanel.this.tileSelectionMoveButton.setChecked(false);
        if (TileSelectionPanel.this.tileDeleteButton.isChecked()) {
          for (TileButton tileButton : TileSelectionPanel.this.buttons) {
            tileButton.setSelected(false);
          }
        }
      }
    });

    this.panel.add(this.tileDeleteButton);
    this.undoButton = new UndoButton();
    this.undoButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        fireUndoEvent();
      }
    });
    this.panel.add(this.undoButton);
    this.tileSelectionMoveButton = new TileSelectionMoveButton();
    this.tileSelectionMoveButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        TileSelectionPanel.this.tileSelectionMoveButton
            .setChecked(!TileSelectionPanel.this.tileSelectionMoveButton.isChecked());
        TileSelectionPanel.this.tileDeleteButton.setChecked(false);
        if (TileSelectionPanel.this.tileSelectionMoveButton.isChecked()) {
          for (TileButton tileButton : TileSelectionPanel.this.buttons) {
            tileButton.setSelected(false);
          }
        } else {
          fireUnselectionEvent();
        }
      }
    });

    this.panel.add(this.tileSelectionMoveButton);
    this.tileSelectionDropButton = new TileSelectionDropButton();
    this.tileSelectionDropButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        fireDropEvent();
        fireUnselectionEvent();
        TileSelectionPanel.this.tileSelectionMoveButton.setChecked(false);
      }
    });
    this.panel.add(this.tileSelectionDropButton);
    addButton(this.westWatchBlockButton, "Watchblock");
    addButton(this.northWatchBlockButton, "Watchblock");
    addButton(this.westStellBlockButton, "Stellblock");
    addButton(this.northStellBlockButton, "Stellblock");
    addButton(this.westBlockButton, "Block");
    addButton(this.northBlockButton, "Block");
    addButton(this.westTrackButton, "Track");
    addButton(this.northTrackButton, "Track");
    addButton(this.northCurve, "Curve");
    addButton(this.westCurve, "Curve");
    addButton(this.southCurve, "Curve");
    addButton(this.eastCurve, "Curve");

    addButton(this.northLeftSwitch, "Left switch");
    addButton(this.westLeftSwitch, "Left switch");
    addButton(this.southLeftSwitch, "Left switch");
    addButton(this.eastLeftSwitch, "Left switch");

    addButton(this.northRightSwitch, "Right switch");
    addButton(this.westRightSwitch, "Right switch");
    addButton(this.southRightSwitch, "Right switch");
    addButton(this.eastRightSwitch, "Right switch");
  }

  private void selectButton(TileButton button) {
    for (TileButton tileButton : this.buttons) {
      tileButton.setSelected(tileButton == button);
    }
  }

  private void addButton(TileButton button, String toolTip) {
    this.buttons.add(button);
    this.panel.add(button);
    button.setToolTipText(toolTip);
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent arg0) {
        TileSelectionPanel.this.tileDeleteButton.setChecked(false);
        TileSelectionPanel.this.tileSelectionMoveButton.setChecked(false);
        selectButton(button);
      }
    });
  }

  @Override
  public boolean isAddTileMode() {
    for (TileButton tileButton : this.buttons) {
      if (tileButton.isSelected()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isDeleteTileMode() {
    return this.tileDeleteButton.isChecked();
  }

  @Override
  public boolean isSelectionMoveMode() {
    return this.tileSelectionMoveButton.isChecked();
  }

  @Override
  public Optional<Tile> produceSelectedTile() {
    for (TileButton tileButton : this.buttons) {
      if (tileButton.isSelected()) {
        return Optional.of(tileButton.createTile());
      }
    }
    return Optional.empty();
  }

  private void fireUndoEvent() {
    for (TileModelListener undoListener : this.undoListeners) {
      undoListener.undo();
    }
  }

  private void fireUnselectionEvent() {
    for (TileModelListener undoListener : this.undoListeners) {
      undoListener.unselect();
    }
  }

  private void fireDropEvent() {
    for (TileModelListener undoListener : this.undoListeners) {
      undoListener.dropSelection();
    }
  }

  @Override
  public void addModelListener(TileModelListener listener) {
    this.undoListeners.add(listener);
  }

  @Override
  public void removeModelListener(TileModelListener listener) {
    this.undoListeners.remove(listener);
  }
}
