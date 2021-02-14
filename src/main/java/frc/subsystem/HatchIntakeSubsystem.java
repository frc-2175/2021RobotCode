package frc.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.MotorWrapper;
import frc.PIDController;
import frc.ServiceLocator;
import frc.SolenoidWrapper;
import frc.info.RobotInfo;
import frc.info.SmartDashboardInfo;

public class HatchIntakeSubsystem {
	private MotorWrapper frontIntakeWheel; // TODO(medium): As many of these as possible should be final.
	private MotorWrapper groundRollerBallMotor; // TODO(low): What is a roller ball motor? :P
	private MotorWrapper groundActuationMotor;
	private SolenoidWrapper actuatorHatchSolenoid;
	private PIDController pidController;
	private final SmartDashboardInfo smartDashboardInfo;
	private boolean isManual;
	private double setpoint;
	private int zeroEncoder;
	private double output;

	public HatchIntakeSubsystem() {
		ServiceLocator.register(this);

		RobotInfo robotInfo = ServiceLocator.get(RobotInfo.class);
		smartDashboardInfo = ServiceLocator.get(SmartDashboardInfo.class);

		frontIntakeWheel = robotInfo.get(RobotInfo.SWAN);
		actuatorHatchSolenoid = robotInfo.get(RobotInfo.HATCH_ACTUATOR_SOLENOID);
		double kp = smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_PID_P);
		double ki = smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_PID_I);
		double kd = smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_PID_D);
		pidController = new PIDController(kp, ki, kd);
		pidController.clear(Timer.getFPGATimestamp());
		isManual = false;
	}

	public void setIsManual(boolean isManual) {
		this.isManual = isManual;
	}

	public void spinInFront() { //spin in front/main intake
		frontIntakeWheel.set(smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_INTAKE_SPIN_IN_FRONT));
	}
	public void spinOutFront() { //spin out front/main intake
		frontIntakeWheel.set(smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_INTAKE_SPIN_OUT_FRONT));
	}
	public void stopSpinning() { // stops front rolling
		frontIntakeWheel.set(0);
	}

	public void toggleFrontIntake() {
		actuatorHatchSolenoid.set(!actuatorHatchSolenoid.get());
	}

	public void setFrontIntakeOut() {
		actuatorHatchSolenoid.set(true);
	}

	public void teleopPeriodic() {
		pidController.updateTime(Timer.getFPGATimestamp());
	}

	public static double clamp(double val, double min, double max) {
		return val >= min && val <= max ? val : (val < min ? min : max);
	}
}
