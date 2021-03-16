import static org.junit.Assert.assertEquals;
import org.junit.Test;
import frc.subsystem.ControlPanelSubsystem;

public class ControlPanelSubsystemTest {
    @Test
    public void testGetControlPanelColor() {
        assertEquals("red", ControlPanelSubsystem.getControlPanelColor(10));
        assertEquals("yellow", ControlPanelSubsystem.getControlPanelColor(60));
        assertEquals("green", ControlPanelSubsystem.getControlPanelColor(110));
        assertEquals("cyan", ControlPanelSubsystem.getControlPanelColor(200));
        assertEquals("red", ControlPanelSubsystem.getControlPanelColor(270));
        assertEquals("red", ControlPanelSubsystem.getControlPanelColor(271));
        
    }
}