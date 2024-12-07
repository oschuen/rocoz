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

import rocsim.schedule.Loco;
import rocsim.schedule.model.LocoModel;
import rocsim.schedule.model.ScheduleModel;
import rocsim.schedule.model.TrackPlanModel;
import rocsim.schedule.model.TrackPlanModel.Direction;
import rocsim.schedule.model.TripModel;

public class ReadPlan {

  private List<Loco> locos = new ArrayList<>();
  private List<LocoModel> locoModels = new ArrayList<>();
  private List<TripModel> tripModels = new ArrayList<>();

  /**
   * @return the trackModel
   */
  public TrackPlanModel getTrackModel() {
    return this.trackModel;
  }

  private TrackPlanModel trackModel = new TrackPlanModel();

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

        TripModel tripModel = new TripModel();
        tripModel.setId(id);
        tripModel.setLocoId(trainId);
        boolean firstStart = true;
        NodeList segmentChilds = child.getChildNodes();
        ScEntry entryK1 = null;
        for (int j = 0; j < segmentChilds.getLength(); j++) {
          Node segmentChild = segmentChilds.item(j);
          if ((segmentChild.getNodeType() == Node.ELEMENT_NODE) && segmentChild.getNodeName().equals("scentry")) {
            ScEntry entry = readScEntry(segmentChild);
            if (entryK1 != null) {
              if (firstStart) {
                tripModel.setStartTime(entryK1.departTime);
              }
              ScheduleModel schedModel = new ScheduleModel();
              schedModel.setStartBlock(entryK1.block);
              schedModel.setEndBlock(entry.block);
              schedModel.setDuration(entry.arrivalTime - entryK1.departTime);
              schedModel.setPause(entry.minWaitTime);
              tripModel.addSchedule(schedModel);
            }
            entryK1 = entry;
          }
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
    this.trackModel.addCurve(id, x, y, dir);
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
    this.trackModel.addTrack(id, x, y, dir);
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
    if (stellBlock) {
      this.trackModel.addStellBlock(id, x, y, dir);
    } else {
      this.trackModel.addBlock(id, x, y, dir);
    }
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
    this.trackModel.addRightSwitch(id, x, y, dir);
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
    this.trackModel.addLeftSwitch(id, x, y, dir);
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
}