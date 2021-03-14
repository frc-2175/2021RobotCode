package command.autonomous;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import frc.command.autonomous.ControlPanelColorCommand;

public class SpinControlPanelCommandTest {
   @Test
   public void testGetShortestDistance(){
        assertEquals(-45, ControlPanelColorCommand.getShortestDistance("red", "green"), 0.001);
        assertEquals(0, ControlPanelColorCommand.getShortestDistance("green", "yellow"), 0.001);
        assertEquals(-45, ControlPanelColorCommand.getShortestDistance("green", "blue"), 0.001);
        assertEquals(90, ControlPanelColorCommand.getShortestDistance("green", "green"), 0.001);
        assertEquals(45, ControlPanelColorCommand.getShortestDistance("yellow", "blue"), 0.001);
   }
}