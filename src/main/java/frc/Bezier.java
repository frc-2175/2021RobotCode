package frc;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Bezier {
    public double x0 = 0;
    public double y0 = 0;
    public double x1, y1, x2, y2, x3, y3;

    public Bezier(double targetx, double targety, double strength1, double strength2, double theta) {
        x3 = targetx;
        y3 = targety;
        x1 = 0;
        y1 = strength1;
        x2 = targetx + Math.sin(Math.toRadians(theta)) * strength2;
		y2 = targety - Math.cos(Math.toRadians(theta)) * strength2;
		publishHandles();
    }

    public Vector[] generateBezier(int resolution) {
        Vector[] points = new Vector[resolution];
        for(int i = 0; i < resolution; i++) {
            points[i] = bezierParametric((double) i / (resolution - 1));
        }
        return points;
    }

    public Vector bezierParametric(double t) {
        double x = x0 + 3 * t * (x1 - x0) + 3 * Math.pow(t, 2) * (x0 + x2 - 2 * x1)
            + Math.pow(t, 3) * (x3 - x0 + 3 * x1 - 3 * x2);
        double y = y0 + 3 * t * (y1 - y0) + 3 * Math.pow(t, 2) * (y0 + y2 - 2 * y1)
            + Math.pow(t, 3) * (y3 - y0 + 3 * y1 - 3 * y2);
        return new Vector(x, y);
	}

	public void publishHandles() {
		SmartDashboard.putNumber("Bezier/x1", x1);
		SmartDashboard.putNumber("Bezier/y1", y1);
		SmartDashboard.putNumber("Bezier/x2", x2);
		SmartDashboard.putNumber("Bezier/y2", y2);
		SmartDashboard.putNumber("Bezier/x3", x3);
		SmartDashboard.putNumber("Bezier/y3", y3);
	}

	public static Vector[] getSamplePath() {
		Bezier bezier = new Bezier(36, 60, 12, 24, 0);
		return bezier.generateBezier(30);
	}
}
