import static org.junit.Assert.assertEquals;

import org.junit.Test;

import frc.math.DrivingUtility.Path;
import frc.math.Vector;
import frc.subsystem.DrivetrainSubsystem;

public class DrivetrainSubsystemTest {
    Path closestPointTestPath = new Path();
    @Test
    public void testFindClosestPoint() {
        closestPointTestPath.path = new Vector[] {new Vector(0,0), new Vector(1,0), new Vector(2,0),new Vector(3,0)};
        closestPointTestPath.numberOfActualPoints = closestPointTestPath.path.length;
        assertEquals(2, DrivetrainSubsystem.findClosestPoint(closestPointTestPath, new Vector(2,1)));
        
    }
    
    @Test
    public void testFindGoalPoint() {
        closestPointTestPath.path = new Vector[] {new Vector(0,0), new Vector(1,0), new Vector(2,0),new Vector(3,0)};
        closestPointTestPath.numberOfActualPoints = closestPointTestPath.path.length;
        assertEquals(3, DrivetrainSubsystem.findGoalPoint(closestPointTestPath, new Vector(2,1), 10));
        assertEquals(1, DrivetrainSubsystem.findGoalPoint(closestPointTestPath, new Vector(0,1), 1));
        
    }
}