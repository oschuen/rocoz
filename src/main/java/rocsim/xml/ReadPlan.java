package rocsim.xml;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
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

public class ReadPlan {

  /**
   * @return the tiles
   */
  public List<Tile> getTiles() {
    return this.tiles;
  }

  private List<Tile> tiles = new ArrayList<>();

  private void addCurve(int x, int y, String orientation) {
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

    this.tiles.add(new Curve(x, y, dir));
  }

  private void addTrack(int x, int y, String orientation) {
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

    this.tiles.add(new Track(x, y, dir));
  }

  private void addBlock(int x, int y, String orientation) {
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

    this.tiles.add(new Block(x, y, dir));
  }

  private void readTk(Node tk) {
    NamedNodeMap attributes = tk.getAttributes();
    String type = "field";
    int x = 0;
    int y = 0;
    String orientation = "west";
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("type")) {
        type = attr.getNodeValue();
      } else if (attr.getNodeName().equals("x")) {
        x = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("y")) {
        y = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("ori")) {
        orientation = attr.getNodeValue();
      }
    }
    if (type.equals("curve")) {
      addCurve(x, y, orientation);
    } else if (type.equals("straight")) {
      addTrack(x, y, orientation);
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

  private void addRightSwitch(int x, int y, String orientation) {
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

    this.tiles.add(new RightSwitch(x, y, dir));
  }

  private void addLeftSwitch(int x, int y, String orientation) {
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

    this.tiles.add(new LeftSwitch(x, y, dir));
  }

  private void readSw(Node sw) {
    NamedNodeMap attributes = sw.getAttributes();
    String type = "left";
    int x = 0;
    int y = 0;
    String orientation = "west";
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("type")) {
        type = attr.getNodeValue();
      } else if (attr.getNodeName().equals("x")) {
        x = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("y")) {
        y = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("ori")) {
        orientation = attr.getNodeValue();
      }
    }
    if (type.equals("right")) {
      addRightSwitch(x, y, orientation);
    } else if (type.equals("left")) {
      addLeftSwitch(x, y, orientation);
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

  private void readBk(Node tk) {
    NamedNodeMap attributes = tk.getAttributes();
    int x = 0;
    int y = 0;
    String orientation = "west";
    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      if (attr.getNodeName().equals("x")) {
        x = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("y")) {
        y = Integer.valueOf(attr.getNodeValue());
      } else if (attr.getNodeName().equals("ori")) {
        orientation = attr.getNodeValue();
      }
    }
    addBlock(x, y, orientation);
  }

  private void readBKList(Node swList) {
    NodeList childs = swList.getChildNodes();
    for (int i = 0; i < childs.getLength(); i++) {
      Node child = childs.item(i);
      if (child.getNodeType() == Node.ELEMENT_NODE) {
        if (child.getNodeName().equals("bk")) {
          readBk(child);
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
          readBk(child);
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
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    ReadPlan planner = new ReadPlan();
    planner.readPlan(new File("/home/oliver/bin/Rocrail/fremo/plan.xml"));
    BufferedImage br = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D gr = br.createGraphics();
    gr.setColor(Color.WHITE);
    gr.fillRect(1, 1, 798, 798);

    for (Tile tile : planner.tiles) {
      tile.draw(30, gr);
    }
    try {
      ImageIO.write(br, "PNG", new File("track.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
