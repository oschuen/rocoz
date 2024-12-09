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
package rocsim.schedule.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import rocsim.gui.tiles.Block;
import rocsim.gui.tiles.Block.BlockStatusListener;
import rocsim.gui.tiles.Curve;
import rocsim.gui.tiles.LeftSwitch;
import rocsim.gui.tiles.RightSwitch;
import rocsim.gui.tiles.Tile;

public class TrackPlanModel {
  public enum BlockKind {
    NONE, BLOCK, STELLBLOCK, WATCHBLOCK
  }

  public static enum Direction {
    SOUTH, WEST, NORTH, EAST
  }

  public enum TrackKind {
    CURVE, TRACK, LEFT_SWITCH, RIGHT_SWICH, BLOCK, STELLBLOCK, WATCHBLOCK
  }

  private List<Track> tracks = new ArrayList<>();

  public static class Track {
    public TrackKind kind = TrackKind.TRACK;
    public String id = "";
    public Direction orientation = Direction.NORTH;
    public Point location = new Point(0, 0);
    public float length = 1.0F;

    public JsonObject toJson() {
      JsonObjectBuilder builder = Json.createObjectBuilder();
      builder.add("id", this.id);
      builder.add("dir", this.orientation.toString());
      builder.add("loc_x", this.location.x);
      builder.add("loc_y", this.location.y);
      builder.add("length", this.length);
      builder.add("kind", this.kind.toString());
      return builder.build();
    }

    public void fromJson(JsonObject obj) {
      this.id = obj.getString("id", "");
      this.orientation = Direction.valueOf(obj.getString("dir", "NORTH"));
      int x = obj.getInt("loc_x", 0);
      int y = obj.getInt("loc_y", 0);
      this.location = new Point(x, y);
      this.length = (float) obj.getJsonNumber("length").doubleValue();
      this.kind = TrackKind.valueOf(obj.getString("kind", "TRACK"));
    }
  }

  public void addTrack(Track track) {
    this.tracks.add(track);
  }

  public List<Track> getTracks() {
    return this.tracks;
  }

  private Track baseCreateTrack(TrackKind kind, String id, int x, int y, Direction orientation, float length) {
    Track track = new Track();
    track.kind = kind;
    track.id = id;
    track.location.x = x;
    track.location.y = y;
    track.length = length;
    track.orientation = orientation;
    return track;
  }

  public List<String> getWatchBlockIds() {
    return this.tracks.stream().filter((track) -> track.kind == TrackKind.WATCHBLOCK).map((track) -> {
      return track.id;
    }).filter(str -> !str.isBlank()).collect(Collectors.toList());
  }

  public List<String> getArrivableBlockIds() {
    List<String> ids = this.tracks.stream()
        .filter((track) -> track.kind == TrackKind.BLOCK || track.kind == TrackKind.STELLBLOCK).map((track) -> {
          return track.id;
        }).filter(str -> !str.isBlank()).collect(Collectors.toList());
    Collections.sort(ids);
    return ids;
  }

  public List<Tile> generateTiles() {
    return generateTiles(null);
  }

  public List<Tile> generateTiles(BlockStatusListener listener) {
    List<Tile> tiles = new ArrayList<>();
    for (Track track : this.tracks) {
      switch (track.kind) {
      case BLOCK:
        Block block = new Block(track.id, track.location.x, track.location.y, track.orientation, BlockKind.BLOCK);
        tiles.add(block);
        break;
      case STELLBLOCK:
        Block stellBlock = new Block(track.id, track.location.x, track.location.y, track.orientation,
            BlockKind.STELLBLOCK);
        tiles.add(stellBlock);
        break;
      case WATCHBLOCK:
        Block watchBlock = new Block(track.id, track.location.x, track.location.y, track.orientation,
            BlockKind.WATCHBLOCK);
        if (listener != null) {
          watchBlock.addStatusListener(listener);
        }
        tiles.add(watchBlock);
        break;
      case CURVE:
        tiles.add(new Curve(track.id, track.location.x, track.location.y, track.orientation));
        break;
      case LEFT_SWITCH:
        tiles.add(new LeftSwitch(track.id, track.location.x, track.location.y, track.orientation));
        break;
      case RIGHT_SWICH:
        tiles.add(new RightSwitch(track.id, track.location.x, track.location.y, track.orientation));
        break;
      case TRACK:
        tiles.add(new rocsim.gui.tiles.Track(track.id, track.location.x, track.location.y, track.orientation));
        break;
      default:
        break;
      }
    }

    return tiles;
  }

  public void addCurve(String id, int x, int y, Direction orientation) {
    this.tracks.add(baseCreateTrack(TrackKind.CURVE, id, x, y, orientation, 0.5F));
  }

  public void addTrack(String id, int x, int y, Direction orientation) {
    this.tracks.add(baseCreateTrack(TrackKind.TRACK, id, x, y, orientation, 0.5F));
  }

  public void addLeftSwitch(String id, int x, int y, Direction orientation) {
    this.tracks.add(baseCreateTrack(TrackKind.LEFT_SWITCH, id, x, y, orientation, 0.5F));
  }

  public void addRightSwitch(String id, int x, int y, Direction orientation) {
    this.tracks.add(baseCreateTrack(TrackKind.RIGHT_SWICH, id, x, y, orientation, 0.5F));
  }

  public void addStellBlock(String id, int x, int y, Direction orientation) {
    this.tracks.add(baseCreateTrack(TrackKind.STELLBLOCK, id, x, y, orientation, 0.5F));
  }

  public void addBlock(String id, int x, int y, Direction orientation) {
    this.tracks.add(baseCreateTrack(TrackKind.BLOCK, id, x, y, orientation, 0.5F));
  }

  public JsonArray toJson() {
    JsonArrayBuilder builder = Json.createArrayBuilder();
    for (Track track : this.tracks) {
      builder.add(track.toJson());
    }
    return builder.build();
  }

  public void fromJson(JsonArray arr) {
    this.tracks.clear();
    for (int i = 0; i < arr.size(); i++) {
      Track track = new Track();
      track.fromJson(arr.getJsonObject(i));
      this.tracks.add(track);
    }
  }
}
