package rocksim.gui;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import rocsim.gui.LeftSwitch;
import rocsim.gui.Tile.Direction;

public class LeftSwitchTest {

  @Test
  public void testUnswitched() {
    LeftSwitch weiche = new LeftSwitch("", 3, 4, Direction.NORTH);
    weiche.setSwitched(false);
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

  @Test
  public void testswitched() {
    LeftSwitch weiche = new LeftSwitch("", 3, 4, Direction.NORTH);
    weiche.setSwitched(true);
    assertEquals(Optional.of(Direction.WEST), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.of(Direction.SOUTH), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.SOUTH));
    weiche.setOrientation(Direction.EAST);
    assertEquals(Optional.of(Direction.NORTH), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.of(Direction.WEST), weiche.getFollowUpDirection(Direction.SOUTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.NORTH));
    weiche.setOrientation(Direction.SOUTH);
    assertEquals(Optional.of(Direction.EAST), weiche.getFollowUpDirection(Direction.SOUTH));
    assertEquals(Optional.of(Direction.NORTH), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.NORTH));
    weiche.setOrientation(Direction.WEST);
    assertEquals(Optional.of(Direction.SOUTH), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.of(Direction.EAST), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.SOUTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.EAST));
  }

}
