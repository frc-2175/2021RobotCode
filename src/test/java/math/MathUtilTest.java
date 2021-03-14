import static org.junit.Assert.assertEquals;
import org.junit.Test;
import frc.math.MathUtility;

public class MathUtilTest {
   @Test
   public void testMod(){
        assertEquals(0, MathUtility.mod(4,2), 0.001);
   }

   @Test
   public void testGetDistanceBetweenAngles(){
        assertEquals(0, MathUtility.getDistanceBetweenAngles(0, 0), 0.001);
        assertEquals(60, MathUtility.getDistanceBetweenAngles(0, 60), 0.001);
        assertEquals(50, MathUtility.getDistanceBetweenAngles(10, 60), 0.001);
        assertEquals(40, MathUtility.getDistanceBetweenAngles(0, 400), 0.001);
        assertEquals(30, MathUtility.getDistanceBetweenAngles(10, 400), 0.001);
        assertEquals(-60, MathUtility.getDistanceBetweenAngles(60, 0), 0.001);
        assertEquals(-40, MathUtility.getDistanceBetweenAngles(400, 0), 0.001);
        assertEquals(-90, MathUtility.getDistanceBetweenAngles(0, 270), 0.001);
        assertEquals(-100, MathUtility.getDistanceBetweenAngles(10, 270), 0.001);
        assertEquals(-30, MathUtility.getDistanceBetweenAngles(0, -30), 0.001);
        assertEquals(-40, MathUtility.getDistanceBetweenAngles(10, -30), 0.001);
        assertEquals(60, MathUtility.getDistanceBetweenAngles(270, -30), 0.001);
        assertEquals(100, MathUtility.getDistanceBetweenAngles(270, 10), 0.001);
        assertEquals(20, MathUtility.getDistanceBetweenAngles(0, -700), 0.001);
   }
}