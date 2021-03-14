import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import frc.math.DrivingUtility;
import frc.math.DrivingUtility.Path;
import frc.math.Vector;

public class DrivingUtilityTest {
    @Rule
    public ErrorCollector collector = new ErrorCollector();

    @Test
    public void testGetTrapezoidSpeed() {
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, -1), closeTo(0.2, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 0), closeTo(0.2, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 0.5), closeTo(0.5, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 1), closeTo(0.8, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 2), closeTo(0.8, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 3), closeTo(0.8, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 4), closeTo(0.6, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 5), closeTo(0.4, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 5, 1, 2, 6), closeTo(0.4, 0.01));

        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, -1), closeTo(0.2, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, 0), closeTo(0.2, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, 0.5), closeTo(0.5, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, 1), closeTo(0.8, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, 2), closeTo(0.6, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, 3), closeTo(0.4, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 1, 2, 4), closeTo(0.4, 0.01));

        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 2, 2, -1), closeTo(0.2, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 2, 2, 0), closeTo(0.2, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 2, 2, 1.5), closeTo(0.3, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 2, 2, 3), closeTo(0.4, 0.01));
        collector.checkThat(DrivingUtility.getTrapezoidSpeed(0.2, 0.8, 0.4, 3, 2, 2, 4), closeTo(0.4, 0.01));
    }
    @Test
    public void testMakePathLine() {
        assertArrayEquals(
            new Vector[] {new Vector(1,1), new Vector(2,1), new Vector(3,1), new Vector(3.5, 1)},
            DrivingUtility.makePathLine(new Vector(1,1), new Vector(3.5, 1))
        );
        assertArrayEquals(
            new Vector[] {new Vector(1,1), new Vector(1,2), new Vector(1,3), new Vector(1, 3.5)},
            DrivingUtility.makePathLine(new Vector(1,1), new Vector(1, 3.5))
        );
    }

    @Test
    public void testMakeRightPathArc() {
        double r = 6 / Math.PI;
        Vector[] path = DrivingUtility.makeRightArcPathSegment(r, 95).path;
        assertEquals(new Vector(0,0), path[0]);
        assertEquals(new Vector(r - r*(Math.sqrt(3) / 2) , r*.5), path[1]);
        assertEquals(new Vector(r - r*.5, r*Math.sqrt(3) / 2), path[2]);
        assertEquals(new Vector(r, r), path[3]);
        assertTrue( path[4].x > r);
        assertTrue( path[4].y > r - .7);
    }

    @Test
    public void testMakeLeftPathArc() {
        double r = 6 / Math.PI;
        Vector[] path = DrivingUtility.makeLeftArcPathSegment(r, 95).path;
        assertTrue( path[1].x < 0);
        assertTrue( path[2].x < 0);
        assertTrue( path[3].x < 0);
        assertTrue( path[4].x < 0);

        assertTrue( path[1].y > 0);
        assertTrue( path[2].y > 0);
        assertTrue( path[3].y > 0);
        assertTrue( path[4].y > 0);

    }

    @Test
    public void testMakePath() {
        Path path = DrivingUtility.makePath(false, 0, new Vector(0, 0), 
            new DrivingUtility.PathSegment(-90, new Vector[] {
                new Vector(0, 0),
                new Vector(0, 1),
                new Vector(0, 2)
            }), 
            new DrivingUtility.PathSegment(90, new Vector[] {
                new Vector(0, 0),
                new Vector(0, 1),
                new Vector(0, 2)
            }),
            new DrivingUtility.PathSegment(-90, new Vector[] {
                new Vector(0, 0),
                new Vector(0, 1),
                new Vector(0, 2)
            })
        );
        Vector[] actualPath = new Vector[path.numberOfActualPoints];
        for(int i = 0; i < actualPath.length; i++) {
            actualPath[i] = path.path[i];
        }

        assertArrayEquals(
            new Vector[] {
                new Vector(0, 0),
                new Vector(0, 1),
                new Vector(0, 2),
                new Vector(0, 2),
                new Vector(1, 2),
                new Vector(2, 2),
                new Vector(2, 2),
                new Vector(2, 3),
                new Vector(2, 4),
            }
        , actualPath);
    }

}