package rocsim.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rocsim.gui.Block;
import rocsim.gui.Curve;
import rocsim.gui.LeftSwitch;
import rocsim.gui.RightSwitch;
import rocsim.gui.Tile;
import rocsim.gui.Tile.Direction;
import rocsim.gui.Track;
import rocsim.schedule.Loco;
import rocsim.schedule.Schedule;
import rocsim.schedule.Trip;
import rocsim.schedule.model.LocoModel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TripModel;

public class ReadPlan {

  private List<Tile> tiles = new ArrayList<>();
  private List<Trip> trips = new ArrayList<>();
  private List<Loco> locos = new ArrayList<>();
  private List<LocoModel> locoModels = new ArrayList<>();
  private List<TripModel> tripModels = new ArrayList<>();
  private List<String> blockIds = new ArrayList<>();

  private class ScEntry {
    public int arrivalTime = 0;
    public int departTime = 0;
    public String block = "";
    public int minWaitTime = 0;

    @Override
    public String toString() {
      return "ScEntry [block=" + this.block + ", arrivalTime=" + this.arrivalTime + ", departTime=" + this.departTime
          + ", minWaitTime=" + this.minWaitTime + "]";
    }
  }

  private ScEntry readScEntry(Node sched) {
    NamedNodeMap attributes = sched.getAttributes();
    String block = "";
    int hour = 0;
    int minute = 0;
    int ahour = 0;
    int aminute = 0;
    int minwait = 0;
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("block")) {
        block = attr.getNodeValue();
      } else if (attr.getNodeName().equals("ahour")) {
        ahour = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("aminute")) {
        aminute = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("hour")) {
        hour = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("minute")) {
        minute = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("minwait")) {
        minwait = Integer.valueOf(attr.getNodeValue());
      }
    }
    ScEntry entry = new ScEntry();
    entry.arrivalTime = (ahour * 60 + aminute) * 60;
    entry.departTime = (hour * 60 + minute) * 60;
    entry.block = block;
    entry.minWaitTime = minwait;

    return entry;
  }

  private void readTrip(Node segmentList) {
    NodeList childs = segmentList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        NamedNodeMap attributes = child.getAttributes();
        String id = "";
        String trainId = "";
        for (int j = 0; j < attributes.getLength(); j++) {
          Node attr = attributes.item(j);
          if (attr.getNodeName().equals("id")) {
            id = attr.getNodeValue();
          } else if (attr.getNodeName().equals("trainid")) {
            trainId = attr.getNodeValue();
          }
        }
        Trip trip = new Trip(trainId, id);

        NodeList segmentChilds = child.getChildNodes();
        ScEntry entryK1 = null;
        for (int j = 0; j < segmentChilds.getLength(); j++) {
          Node segmentChild = segmentChilds.item(j);
          if ((segmentChild.getNodeType() == Node.ELEMENT_NODE) && segmentChild.getNodeName().equals("scentry")) {
            ScEntry entry = readScEntry(segmentChild);
            if (entryK1 != null) {
              Schedule schedule = new Schedule(entryK1.block, entry.block, entryK1.departTime, entry.arrivalTime,
                  entry.minWaitTime);
              trip.addSchedule(schedule);
            }
            entryK1 = entry;
          }
        }
        this.trips.add(trip);
        TripModel tripModel = new TripModel();
        tripModel.setId(trip.getId());
        tripModel.setStartTime(trip.getStartTime());
        tripModel.setLocoId(trip.getTrainId());
        for (Schedule schedule : trip.getSchedules()) {
          ScheduleModel schedModel = new ScheduleModel();
          schedModel.setStartBlock(schedule.getStartBlock());
          schedModel.setEndBlock(schedule.getEndBlock());
          schedModel.setDuration((schedule.getEndTime() - schedule.getStartTime()) / 60);
          schedModel.setPause(schedule.getMinWaitTime());
          tripModel.addSchedule(schedModel);
        }
        this.tripModels.add(tripModel);
      }
    }
  }

  private void readOperator(Node tk) {
    NamedNodeMap attributes = tk.getAttributes();
    String id = "";
    String comment = "";
    int vMax = 0;
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("id")) {
        id = attr.getNodeValue();
      } else if (attr.getNodeName().equals("V_max")) {
        vMax = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("desc")) {
        comment = attr.getNodeValue();
      }
    }
    LocoModel model = new LocoModel();
    model.setId(id);
    model.setvMax(vMax);
    model.setComment(comment);
    this.locos.add(new Loco(model));
    this.locoModels.add(model);
  }

  private void readLocos(Node tkList) {
    NodeList childs = tkList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        if (child.getNodeName().equals("operator")) {
          readOperator(child);
        }
      }
    }
  }

  private void addCurve(String id, int x, int y, String orientation) {
    Direction dir = Direction.NORTH;
    if (orientation.equals("north")) {
      dir = Direction.SOUTH;
    } else if (orientation.equals("west")) {
      dir = Direction.WEST;
    } else if (orientation.equals("south")) {
      dir = Direction.NORTH;
    } else if (orientation.equals("east")) {
      dir = Direction.EAST;
    }

    this.tiles.add(new Curve(id, x, y, dir));
  }

  private void addTrack(String id, int x, int y, String orientation) {
    Direction dir = Direction.NORTH;
    if (orientation.equals("north")) {
      dir = Direction.NORTH;
    } else if (orientation.equals("west")) {
      dir = Direction.WEST;
    } else if (orientation.equals("south")) {
      dir = Direction.SOUTH;
    } else if (orientation.equals("east")) {
      dir = Direction.EAST;
    }

    this.tiles.add(new Track(id, x, y, dir));
  }

  private void addBlock(String id, int x, int y, String orientation, boolean stellBlock) {
    Direction dir = Direction.NORTH;
    if (orientation.equals("north")) {
      dir = Direction.NORTH;
    } else if (orientation.equals("west")) {
      dir = Direction.WEST;
    } else if (orientation.equals("south")) {
      dir = Direction.SOUTH;
    } else if (orientation.equals("east")) {
      dir = Direction.EAST;
    }

    this.tiles.add(new Block(id, x, y, dir, stellBlock));
  }

  private void readTk(Node tk) {
    NamedNodeMap attributes = tk.getAttributes();
    String type = "field";
    String id = "";
    int x = 0;
    int y = 0;
    String orientation = "west";
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("type")) {
        type = attr.getNodeValue();
      } else if (attr.getNodeName().equals("id")) {
        id = attr.getNodeValue();
      } else if (attr.getNodeName().equals("x")) {
        x = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("y")) {
        y = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("ori")) {
        orientation = attr.getNodeValue();
      }
    }
    if (type.equals("curve")) {
      addCurve(id, x, y, orientation);
    } else if (type.equals("straight")) {
      addTrack(id, x, y, orientation);
    }
  }

  private void readTkList(Node tkList) {
    NodeList childs = tkList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        if (child.getNodeName().equals("tk")) {
          readTk(child);
        }
      }
    }
  }

  private void addRightSwitch(String id, int x, int y, String orientation) {
    Direction dir = Direction.NORTH;
    if (orientation.equals("north")) {
      dir = Direction.NORTH;
    } else if (orientation.equals("west")) {
      dir = Direction.EAST;
    } else if (orientation.equals("south")) {
      dir = Direction.SOUTH;
    } else if (orientation.equals("east")) {
      dir = Direction.WEST;
    }

    this.tiles.add(new RightSwitch(id, x, y, dir));
  }

  private void addLeftSwitch(String id, int x, int y, String orientation) {
    Direction dir = Direction.NORTH;
    if (orientation.equals("north")) {
      dir = Direction.SOUTH;
    } else if (orientation.equals("west")) {
      dir = Direction.WEST;
    } else if (orientation.equals("south")) {
      dir = Direction.NORTH;
    } else if (orientation.equals("east")) {
      dir = Direction.EAST;
    }

    this.tiles.add(new LeftSwitch(id, x, y, dir));
  }

  private void readSw(Node sw) {
    NamedNodeMap attributes = sw.getAttributes();
    String type = "left";
    String id = "";
    int x = 0;
    int y = 0;
    String orientation = "west";
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("type")) {
        type = attr.getNodeValue();
      } else if (attr.getNodeName().equals("id")) {
        id = attr.getNodeValue();
      } else if (attr.getNodeName().equals("x")) {
        x = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("y")) {
        y = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("ori")) {
        orientation = attr.getNodeValue();
      }
    }
    if (type.equals("right")) {
      addRightSwitch(id, x, y, orientation);
    } else if (type.equals("left")) {
      addLeftSwitch(id, x, y, orientation);
    }
  }

  private void readSWList(Node swList) {
    NodeList childs = swList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        if (child.getNodeName().equals("sw")) {
          readSw(child);
        }
      }
    }
  }

  private void readBk(Node tk, boolean stellBlock) {
    NamedNodeMap attributes = tk.getAttributes();
    int x = 0;
    int y = 0;
    String id = "";
    String orientation = "west";
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("x")) {
        x = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("id")) {
        id = attr.getNodeValue();
      } else if (attr.getNodeName().equals("y")) {
        y = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("ori")) {
        orientation = attr.getNodeValue();
      }
    }
    addBlock(id, x, y, orientation, stellBlock);
    this.blockIds.add(id);
  }

  private void readBKList(Node swList) {
    NodeList childs = swList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        if (child.getNodeName().equals("bk")) {
          readBk(child, false);
        }
      }
    }
  }

  private void readSBList(Node swList) {
    NodeList childs = swList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        if (child.getNodeName().equals("sb")) {
          readBk(child, true);
        }
      }
    }
  }

  public void readPlan(File f) {
    String rawDoc;
    try {
      rawDoc = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setValidating(false);
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, false);
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new ByteArrayInputStream(rawDoc.getBytes()));
      Node root = doc.getDocumentElement();

      NodeList childs = root.getChildNodes();
      for (int i = 0; i < childs.getLength(); i++) {
        Node subNode = childs.item(i);
        if (subNode.getNodeType() == Node.ELEMENT_NODE) {
          if (subNode.getNodeName().equals("tklist")) {
            readTkList(subNode);
          } else if (subNode.getNodeName().equals("swlist")) {
            readSWList(subNode);
          } else if (subNode.getNodeName().equals("bklist")) {
            readBKList(subNode);
          } else if (subNode.getNodeName().equals("sblist")) {
            readSBList(subNode);
          } else if (subNode.getNodeName().equals("sclist")) {
            readTrip(subNode);
          } else if (subNode.getNodeName().equals("operatorlist")) {
            readLocos(subNode);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @return the tiles
   */
  public List<Tile> getTiles() {
    return this.tiles;
  }

  /**
   * @return the trips
   */
  public List<Trip> getTrips() {
    return this.trips;
  }

  /**
   * @return the locos
   */
  public List<Loco> getLocos() {
    return this.locos;
  }

  /**
   * @return the loco Models
   */
  public List<LocoModel> getLocoModels() {
    return this.locoModels;
  }

  /**
   * @return the tripModels
   */
  public List<TripModel> getTripModels() {
    return this.tripModels;
  }

  public List<String> getBlockIds() {
    return this.blockIds;
  }
}
