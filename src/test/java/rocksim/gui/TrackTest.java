package rocksim.gui;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import rocsim.gui.Tile.Direction;
import rocsim.gui.Track;

public class TrackTest {

  @Test
  public void testFollowUp() {
    Track weiche = new Track("", 3, 4, Direction.NORTH);
    assertEquals(Optional.of(Direction.NORTH), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.of(Direction.SOUTH), weiche.getFollowUpDirection(Direction.SOUTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.WEST));
    weiche.setOrientation(Direction.EAST);
    assertEquals(Optional.of(Direction.EAST), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.of(Direction.WEST), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.SOUTH));
  }

}
