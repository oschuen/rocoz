package rocsim.gui.editor;

import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import rocsim.schedule.time.TimeModel;
import rocsim.xml.ReadPlan;

public class EditorContainer {

  private LocoFrame locoFrame;
  private TripFrame tripFrame;
  private StringListDataModel blockIdDataModel = new StringListDataModel();
  private StringListDataModel locoIdDataModel = new StringListDataModel();

  public EditorContainer(ReadPlan planner, TimeModel timeModel) {

    this.blockIdDataModel.setValueList(planner.getBlockIds());
    //
    this.locoFrame = new LocoFrame();
    this.locoFrame.setLocoModels(planner.getLocoModels());
    this.locoIdDataModel.setValueList(this.locoFrame.getLocoIds());
    this.locoFrame.addDataListener(new ListDataListener() {

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
        List<String> ids = EditorContainer.this.locoFrame.getLocoIds();
        EditorContainer.this.locoIdDataModel.setValueList(ids);
      }
    });

    this.tripFrame = new TripFrame(timeModel, this.locoIdDataModel, this.blockIdDataModel);
    this.tripFrame.setTripModels(planner.getTripModels());
  }

  /**
   * @return the locoFrame
   */
  public JScrollPane getLocoFrame() {
    return new JScrollPane(this.locoFrame);
  }

  /**
   * @return the tripFrame
   */
  public JScrollPane getTripFrame() {
    return new JScrollPane(this.tripFrame);
  }
}
