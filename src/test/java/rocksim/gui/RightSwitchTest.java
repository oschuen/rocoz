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
package rocksim.gui;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import rocsim.gui.tiles.RightSwitch;
import rocsim.schedule.model.TrackPlanModel.Direction;

public class RightSwitchTest {

  @Test
  public void testUnswitched() {
    RightSwitch weiche = new RightSwitch("", 3, 4, 0.5F, Direction.NORTH);
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
    RightSwitch weiche = new RightSwitch("", 3, 4, 0.5F, Direction.NORTH);
    weiche.setSwitched(true);
    assertEquals(Optional.of(Direction.EAST), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.of(Direction.SOUTH), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.SOUTH));
    weiche.setOrientation(Direction.EAST);
    assertEquals(Optional.of(Direction.SOUTH), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.of(Direction.WEST), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.SOUTH));
    weiche.setOrientation(Direction.SOUTH);
    assertEquals(Optional.of(Direction.WEST), weiche.getFollowUpDirection(Direction.SOUTH));
    assertEquals(Optional.of(Direction.NORTH), weiche.getFollowUpDirection(Direction.EAST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.NORTH));
    weiche.setOrientation(Direction.WEST);
    assertEquals(Optional.of(Direction.NORTH), weiche.getFollowUpDirection(Direction.WEST));
    assertEquals(Optional.of(Direction.EAST), weiche.getFollowUpDirection(Direction.SOUTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.NORTH));
    assertEquals(Optional.empty(), weiche.getFollowUpDirection(Direction.EAST));
  }

}
