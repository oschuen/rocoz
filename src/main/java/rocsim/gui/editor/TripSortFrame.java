package rocsim.gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import rocsim.gui.widgets.ListFrame;
import rocsim.schedule.model.TripModel;

public class TripSortFrame extends JPanel {
  private static final long serialVersionUID = 1L;
  private final ButtonGroup buttonGroup = new ButtonGroup();
  private InnerFrame tripFrame;
  private ActionListener sortActionListener;
  private JRadioButton rdbtnTime;
  private JRadioButton rdbtnLocoTime;
  private JRadioButton rdbtnTripId;

  public TripSortFrame(EditorContext context) {
    setLayout(new BorderLayout(0, 0));
    this.sortActionListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        resortTrips();
      }
    };

    JPanel panel = new JPanel();
    FlowLayout flowLayout = (FlowLayout) panel.getLayout();
    flowLayout.setVgap(10);
    add(panel, BorderLayout.NORTH);

    this.rdbtnTime = new JRadioButton("Time");
    this.buttonGroup.add(this.rdbtnTime);
    this.rdbtnTime.setSelected(true);
    this.rdbtnTime.addActionListener(this.sortActionListener);
    panel.add(this.rdbtnTime);

    this.rdbtnLocoTime = new JRadioButton("Loco + Time");
    this.buttonGroup.add(this.rdbtnLocoTime);
    this.rdbtnLocoTime.addActionListener(this.sortActionListener);
    panel.add(this.rdbtnLocoTime);

    this.rdbtnTripId = new JRadioButton("Trip ID");
    this.buttonGroup.add(this.rdbtnTripId);
    this.rdbtnTripId.addActionListener(this.sortActionListener);
    panel.add(this.rdbtnTripId);

    JScrollPane scrollPane = new JScrollPane();
    add(scrollPane, BorderLayout.CENTER);

    this.tripFrame = new InnerFrame(context);
    scrollPane.setViewportView(this.tripFrame);
  }

  private void sort(List<TripModel> tripsModels) {
    Comparator<TripModel> comparator;
    if (this.rdbtnTime.isSelected()) {
      comparator = new Comparator<TripModel>() {
        @Override
        public int compare(TripModel tripA, TripModel tripB) {
          return Integer.compare(tripA.getStartTime(), tripB.getStartTime());
        }
      };
    } else if (this.rdbtnLocoTime.isSelected()) {
      comparator = new Comparator<TripModel>() {
        @Override
        public int compare(TripModel tripA, TripModel tripB) {
          int comp = tripA.getLocoId().compareTo(tripB.getLocoId());
          if (comp == 0) {
            comp = Integer.compare(tripA.getStartTime(), tripB.getStartTime());
          }
          return comp;
        }
      };
    } else {
      comparator = new Comparator<TripModel>() {
        @Override
        public int compare(TripModel tripA, TripModel tripB) {
          return tripA.getId().compareTo(tripB.getId());
        }
      };
    }
    Collections.sort(tripsModels, comparator);
  }

  private void resortTrips() {
    List<TripModel> tripsModels = this.tripFrame.getTripModels();
    sort(tripsModels);
    this.tripFrame.setTripModels(tripsModels);
  }

  private static class TripPanelFactory implements rocsim.gui.widgets.ListFrame.ListItemFactory<TripStationPanel> {
    private EditorContext context;

    TripPanelFactory(EditorContext context) {
      this.context = context;
    }

    @Override
    public TripStationPanel createNewItem() {
      return new TripStationPanel(this.context);
    }
  };

  public void setTripModels(List<TripModel> tripModels) {
    sort(tripModels);
    this.tripFrame.setTripModels(tripModels);
  }

  public List<TripModel> getTripModels() {
    return this.tripFrame.getTripModels();
  }

  public void updateContext() {
    this.tripFrame.updateContext();
  }

  public void updateModel(String tripId, TripModel updatedModel) {
    this.tripFrame.updateModel(tripId, updatedModel);
  }

  private class InnerFrame extends ListFrame<TripStationPanel> {
    private static final long serialVersionUID = 1L;
    private EditorContext context;

    public InnerFrame(EditorContext context) {
      super(new TripPanelFactory(context));
      this.context = context;
    }

    public void setTripModels(List<TripModel> tripModels) {
      List<TripStationPanel> panels = new ArrayList<>();
      for (TripModel tripModel : tripModels) {
        TripStationPanel panel = new TripStationPanel(this.context);
        panel.setModel(tripModel);
        panels.add(panel);
      }
      setContent(panels);
      javax.swing.SwingUtilities.invokeLater(() -> {
        revalidate();
        repaint();
      });
    }

    public List<TripModel> getTripModels() {
      List<TripModel> models = new ArrayList<>();
      for (TripStationPanel trip : getContent()) {
        models.add(trip.getModel());
      }

      Collections.sort(models, (tripA, tripB) -> tripA.getId().compareTo(tripB.getId()));
      return models;
    }

    public void updateContext() {
      for (TripStationPanel trip : getContent()) {
        trip.updateContext();
      }
    }

    public void updateModel(String tripId, TripModel updatedModel) {
      for (TripStationPanel trip : getContent()) {
        TripModel tripModel = trip.getModel();
        if (tripModel.getId().equals(tripId)) {
          trip.setModel(updatedModel);
        }
      }
    }
  }

  public void addModel(TripModel updatedModel) {
    List<TripModel> currentTrips = getTripModels();
    currentTrips.add(updatedModel);
    setTripModels(currentTrips);
  }
}
