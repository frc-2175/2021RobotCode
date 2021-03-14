package frc.math;

public class MathUtility {
    public static double mod(double a, double b) {
        return ((a % b) + b) % b;
    }
      
    public static double getDistanceBetweenAngles(double startAngle, double endAngle) {
        startAngle = (startAngle % 360);
        while (startAngle < 0) {
            startAngle = startAngle + 360;
        }
        endAngle = (endAngle % 360);
        while (endAngle < 0) {
            endAngle = endAngle + 360;
        }
        double difference = endAngle - startAngle;
        if (difference > 180) {
            difference = difference - 360; 
        } else if (difference < -180) {
            difference = difference + 360;
        }
        return difference; 

    }

    /**
	 * Clamps a double value based on a minimum and a maximum
	 *
	 * @param val the value to clamp
	 * @param min the minimum to clamp on
	 * @param max the maximum to clamp on
	 * @return min if val is less than min or max if val is greater than max
	 */
	public static double clamp(double val, double min, double max) {
		return val >= min && val <= max ? val : (val < min ? min : max);
	}

	/**
	 * Linearly interpolates between two points based on a t value
	 *
	 * @param a the point to interpolate from
	 * @param b the point to interpolate to
	 * @param t the value to interpolate on
	 * @return an output based on the formula lerp(a, b, t) = (1-t)a + tb
	 */
	public static double lerp(double a, double b, double t) {
		return (1 - t) * a + t * b;
    }
    
    /**
     * 
     * @param value the value you're deadbanding / want to limit
     * @param deadband the amount you want to make a dead zone
     * @return value after limiting (0 if in deadband range, else original value)
     */
    public static double deadband(double value, double deadband) {
		if (Math.abs(value) > deadband) {
			if (value > 0.0) {
				return (value - deadband) / (1.0 - deadband);
			} else {
				return (value + deadband) / (1.0 - deadband);
			}
		} else {
			return 0.0;
		}
	}

	public static double squareInputs(double input) {
		return Math.copySign(input * input, input); 
	}
}