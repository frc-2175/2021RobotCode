package frc.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.MotorWrapper;
import frc.PIDController;
import frc.ServiceLocator;
import frc.info.RobotInfo;
import frc.info.SmartDashboardInfo;

/*
goToBottomPanel
goToBottomCargo
goToMiddlePanel
goToTopPanel
goToMiddleCargo
goToTopCargo

setMode(automatic or manual)

manualMove
*/

public class ElevatorSubsystem {
	private final SmartDashboardInfo smartDashboardInfo;
	private final MotorWrapper elevatorMotor;
	private final MotorWrapper elevatorMotorFollower;
    private PIDController pidController;
	private double setpoint;
	private boolean isManual;
	private boolean stickMoved;
	private final double elevatorKP;
	private final double elevatorKI;
	private final double elevatorKD;
	private final double setpointThreshold;
	private double[] cargoSetpoints = {16, 44, 74};
	private double[] hatchSetpoints = {30, 58};
	// hatch originally 28 & 56

    public ElevatorSubsystem() {
        ServiceLocator.register(this);

		RobotInfo robotInfo = ServiceLocator.get(RobotInfo.class);
		smartDashboardInfo = ServiceLocator.get(SmartDashboardInfo.class);

		elevatorMotor = robotInfo.get(RobotInfo.ELEVATOR_MOTOR_MASTER);
		elevatorMotorFollower = robotInfo.get(RobotInfo.ELEVATOR_MOTOR_FOLLOWER);
		elevatorMotorFollower.follow(elevatorMotor);
		elevatorMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
		elevatorMotor.setSelectedSensorPosition(0, 0, 0);
		zeroEncoder();

		setpointThreshold = 6;
		elevatorKP = smartDashboardInfo.getNumber(SmartDashboardInfo.ELEVATOR_PID_P);
		elevatorKI = smartDashboardInfo.getNumber(SmartDashboardInfo.ELEVATOR_PID_I);
		elevatorKD = smartDashboardInfo.getNumber(SmartDashboardInfo.ELEVATOR_PID_D);
		pidController = new PIDController(elevatorKP, elevatorKI, elevatorKD);

	}
	public void setIsManual(boolean x) {
		isManual = x;
	}
	public void setStickMoved(boolean x) {
		stickMoved = x;
	}

    public void manualMove(double motorSpeed) {
		if(isManual) {
			elevatorMotor.set(motorSpeed);
		}
    }

	public void setElevator() {
		if (!isManual) {
			SmartDashboard.putNumber("setpoint", setpoint);
			double output = pidController.pid(getElevatorPosition(), setpoint, 4); //what to set motor speed to
			output += 0.125;
			output = clamp(output, -0.4, 0.6);
			elevatorMotor.set(output); //setting motor speed to speed needed to go to setpoint
			SmartDashboard.putNumber("AutoPopulate/ElevatorOutput", output);
		}
	}
	public void CargoPlaceElevatorShip() {
		setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_SHIP_SETPOINT);
	}

	// TODO(medium): Are these methods used anywhere? Can they be removed?

    public void CargoPlaceElevatorTop() {
        setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_TOP_SETPOINT);
    }

    public void CargoPlaceElevatorMiddle() {
        setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_MIDDLE_SETPOINT);
    }

    public void CargoPlaceElevatorBottom() {
        setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_BOTTOM_SETPOINT);
	}
	public void HatchPlaceElevatorTop() {
        setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_TOP_SETPOINT);
    }

    public void HatchPlaceElevatorMiddle() {
        setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_MIDDLE_SETPOINT);
    }

    public void HatchPlaceElevatorBottom() {
        setpoint = smartDashboardInfo.getNumber(SmartDashboardInfo.HATCH_BOTTOM_SETPOINT);
    }

    public void teleopPeriodic() {
        pidController.updateTime(Timer.getFPGATimestamp());
	}

	public double getCurrentDraw() {
		if(elevatorMotor.isTalon) {
			return ((WPI_TalonSRX)(elevatorMotor.getMotor())).getOutputCurrent();
		} else {
			return 0;
		}
	}

	public double getElevatorPosition() {
		return 3 * -elevatorMotor.getSelectedSensorPosition(0) * 1.273 * Math.PI / 4096; // TODO(low): This looks like some dark magic. We should leave a comment labeling the units.
	}

	public void zeroEncoder() {
		elevatorMotor.setSelectedSensorPosition(0, 0, 0);
	}

	public boolean getIsManual() {
		return isManual;
	}

	// TODO(low): Another copy of clamp. See comment in DrivetrainSubsystem.

	public static double clamp(double val, double min, double max) {
		return val >= min && val <= max ? val : (val < min ? min : max);
	}

	public double getElevatorPreset(double[] setpoints, boolean isUp) {
		double elevatorPosition = getElevatorPosition();
		if(isUp) {
			for(double point : setpoints) {
				if(point - elevatorPosition > setpointThreshold) {
					return point;
					// setpoint = point;
				}
			}
		} else {
			for(int i = (setpoints.length - 1); i >= 0; i--) {
				if(setpoints[i] - elevatorPosition < -setpointThreshold) {
					return setpoints[i];
					// setpoint = setpoints[i];
				}
			}
		}
		return -1;
	}

	public void nextElevatorPreset(double[] setpoints, boolean isUp) {
		double currentSetpoint = setpoint;
		if(isUp) {
			for(int x = 0; x < setpoints.length ; x++) {
				if(currentSetpoint < setpoints[x]) {
					setpoint = setpoints[x];
					break;
				}
			}
		} else {
			for(int x = setpoints.length - 1; x >= 0; x--) {
				if(currentSetpoint > setpoints[x]) {
					setpoint = setpoints[x];
					break;
				}
			}

		}
	}

	public void setSetpoint(double inputPoint) {
		setpoint = inputPoint;
	}

	public double getSetpoint() {
		return setpoint;
	}

	public double[] getCargoSetpoints() {
		return cargoSetpoints;
	}

	public double[] getHatchSetpoints() {
		return hatchSetpoints;
	}

	public boolean getIsElevatorAtBottom() {
		// TODO: this value is arbitrary and should eventually be a SmartDashboard value
		return getElevatorPosition() < 8;
	}
}
