package frc;

public class PIDController {
    public double kp, ki, kd;
	double integral, previousError, previousTime, dt;
	boolean shouldRunIntegral = false;

    /**
     * Constructs a new pid controller with constants
     * @param p proportional gain
     * @param i integral gain
     * @param d derivative gain
     */
    public PIDController(double p, double i, double d) {
        this.kp = p;
        this.ki = i;
        this.kd = d;
        integral = 0;
		previousError = Double.NaN;
		dt = 0;
    }

    /**
     * At any time you want to re-use a PID controller with new
     * constants, call this method.
	 * @param time the time that this clear is being called at
     */
    public void clear(double time) {
		dt = 0;
		previousTime = time;
        integral = 0;
		previousError = Double.NaN;
		shouldRunIntegral = false;
    }

    /**
     * Runs a step of the PID loop with a certain input and setpoint
     * @param input the input to the controller
     * @param setpoint the desired setpoint
     * @return the output of the loop
     */
    public double pid(double input, double setpoint, double threshold) {
        double error = setpoint - input;
		double p = error * kp;
		double i = 0;
		if(shouldRunIntegral) {
            if(((input < threshold + setpoint) && (input > setpoint - threshold)) || (threshold == 0)) { // TODO(low): This condition could probably be reversed for clarity.
                integral += dt * error;
            } else {
                integral = 0;
            }
            i = integral * ki;
		} else {
			shouldRunIntegral = true;
		}
		double d;
		if(Double.isNaN(previousError) || dt == 0) {
			d = 0;
		} else {
			d = ((error - previousError) / dt) * kd;
		}
        previousError = error;
        return p + i + d;
    }

    public double pid(double input, double setpoint) {
        return pid(input, setpoint, 0);
    }
    /**
     * Every time the loop goes forward by one interation, call this
     * method with a new dt.
     * @param time the current time
     */
    public void updateTime(double time) {
		this.dt = time - previousTime;
		previousTime = time;
	}
}