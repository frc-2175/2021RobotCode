package frc.subsystem;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
//import com.ctre.phoenix.music.Orchestra;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.RobotDriveBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.PIDController;
import frc.ServiceLocator;
import frc.VirtualSpeedController;
import frc.info.RobotInfo;
import frc.logging.LogField;
import frc.logging.Logger;
import frc.math.DrivingUtility;
import frc.math.DrivingUtility.Path;
import frc.math.MathUtility;
import frc.math.Vector;

public class DrivetrainSubsystem {
	Logger logger;

    private final RobotInfo robotInfo;
	private final WPI_TalonFX leftMaster;
    private final SpeedController leftFollowerOne;
    private final SpeedController leftFollowerTwo;
	private final WPI_TalonFX rightMaster;
    private final SpeedController rightFollowerOne;
    private final SpeedController rightFollowerTwo;
	private final DifferentialDrive robotDrive;
	public static final double INPUT_THRESHOLD = 0.1; // TODO(low): Constants should move to the top of the class.
	public final AHRS navx;
	double lastEncoderDistanceLeft;
	double lastEncoderDistanceRight;
	private double zeroEncoderLeft; 
	private double zeroEncoderRight;
	public Solenoid gearsSolenoid;
	public static final double TICKS_TO_INCHES = 112.0/182931.0;
	private Vector position = new Vector(0, 0); 
	private PIDController purePursuitPID;

	private SpeedControllerGroup leftMotors;
	private SpeedControllerGroup rightMotors;

	double currentSpeed;

	private static VirtualSpeedController leftVirtualSpeedController = new VirtualSpeedController();
	private static VirtualSpeedController rightVirtualSpeedController = new VirtualSpeedController();
	private static DifferentialDrive virtualRobotDrive;
	private ArrayList<WPI_TalonFX> motorsCollection;
	
	//public Orchestra orchestra;
	
	
    public DrivetrainSubsystem() {
		ServiceLocator.register(this);
		logger = ServiceLocator.get(Logger.class).newWithExtraFields(new LogField("subsystem", "DrivetrainSubsystem"));


		robotInfo = ServiceLocator.get(RobotInfo.class);
		leftMaster = robotInfo.pick(() -> new WPI_TalonFX(15), () -> new WPI_TalonFX(5));
        leftFollowerOne = robotInfo.pick(() -> new WPI_VictorSPX(11), () -> new WPI_TalonSRX(3));
        leftFollowerTwo = robotInfo.pick(() -> new WPI_VictorSPX(10), () -> new WPI_TalonSRX(4));
		rightMaster = robotInfo.pick(() -> new WPI_TalonFX(16), () -> new WPI_TalonFX(2));
        rightFollowerOne = robotInfo.pick(() -> new WPI_VictorSPX(9), () -> new WPI_TalonSRX(0));
		rightFollowerTwo = robotInfo.pick(() -> new WPI_VictorSPX(8), () -> new WPI_TalonSRX(1));
		gearsSolenoid = robotInfo.pick(()-> new Solenoid(2), () -> new Solenoid(4));

		/*  THIS DOESN'T WORK!!!
        leftFollowerOne.follow(leftMaster);
        leftFollowerTwo.follow(leftMaster);
        rightFollowerOne.follow(rightMaster);
		rightFollowerTwo.follow(rightMaster);
		*/

		leftMotors = new SpeedControllerGroup(leftMaster, leftFollowerOne, leftFollowerTwo);
		rightMotors = new SpeedControllerGroup(rightMaster, rightFollowerOne, rightFollowerTwo);

		leftMaster.setInverted(true);
		rightMaster.setInverted(false);
		rightFollowerOne.setInverted(true);
		rightFollowerTwo.setInverted(true);
		leftFollowerOne.setInverted(false);
		leftFollowerTwo.setInverted(false);

		leftMaster.setNeutralMode(NeutralMode.Brake);
		rightMaster.setNeutralMode(NeutralMode.Brake);
		((BaseMotorController) leftFollowerOne).setNeutralMode(NeutralMode.Brake);
		((BaseMotorController) leftFollowerTwo).setNeutralMode(NeutralMode.Brake);
		((BaseMotorController) rightFollowerOne).setNeutralMode(NeutralMode.Brake);
		((BaseMotorController) rightFollowerTwo).setNeutralMode(NeutralMode.Brake);

		


        robotDrive = new DifferentialDrive(leftMotors, rightMotors);
        
        leftVirtualSpeedController = new VirtualSpeedController(); // TODO(low): There is no need to set these here since they are also initialized above.
		rightVirtualSpeedController = new VirtualSpeedController();
		virtualRobotDrive = new DifferentialDrive(leftVirtualSpeedController, rightVirtualSpeedController);

		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);

		lastEncoderDistanceLeft = 0;
		lastEncoderDistanceRight = 0;

		navx = new AHRS(SPI.Port.kMXP);
		navx.reset();
		
		leftMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
		rightMaster.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
		leftMaster.setSelectedSensorPosition(0, 0, 0);
		rightMaster.setSelectedSensorPosition(0, 0, 0);

		purePursuitPID = new PIDController(0.015, 0, 0);

		// orchestra = new Orchestra();
		// orchestra.addInstrument(rightMaster);
		// orchestra.addInstrument(leftMaster);

	}
	
	public void periodic() {
		trackLocation();
		SmartDashboard.putNumber("x", position.x);
		SmartDashboard.putNumber("y", position.y);
		SmartDashboard.putNumber("heading", getHeading());
		SmartDashboard.putNumber("main motor left ", leftMaster.get());
		SmartDashboard.putNumber("main motor right ", rightMaster.get());
		SmartDashboard.putNumber("motor 1 left ", leftFollowerOne.get());
		SmartDashboard.putNumber("motor 2 left ", leftFollowerTwo.get());
		SmartDashboard.putNumber("motor 1 right ", rightFollowerOne.get());
		SmartDashboard.putNumber("motor 2 right ", rightFollowerTwo.get());
		purePursuitPID.updateTime(Timer.getFPGATimestamp());
		SmartDashboard.putNumber("Values/PositionX", position.x);
		SmartDashboard.putNumber("Values/PositionY", position.y);
		SmartDashboard.putNumber("Values/Gyro", navx.getAngle());

	}
    
    public void stopAllMotors() {
           tankDrive(0,0); 
	}
	/**
	 * returns motor values from virtual motor controllers !! Don't use this to drive, this helps blendedDrive!!!
	 * 
	 * @param moveValue how much you want to move forward (value from -1 to 1)
	 * @param turnValue how much you want to turn (value from -1 to 1)
	 * @param inputThreshold built-in deadband
	 * @return
	 */
    public static double[] getBlendedMotorValues(double moveValue, double turnValue, double inputThreshold) {
		virtualRobotDrive.arcadeDrive(moveValue, turnValue, false);
		double leftArcadeValue = leftVirtualSpeedController.get() * 0.8;
		double rightArcadeValue = rightVirtualSpeedController.get()* 0.8;

		virtualRobotDrive.curvatureDrive(moveValue, turnValue, false);
		double leftCurvatureValue = leftVirtualSpeedController.get();
		double rightCurvatureValue = rightVirtualSpeedController.get();

		double lerpT = Math.abs(MathUtility.deadband(moveValue, RobotDriveBase.kDefaultDeadband)) / inputThreshold;
		lerpT = MathUtility.clamp(lerpT, 0, 1);
		double leftBlend = MathUtility.lerp(leftArcadeValue, leftCurvatureValue, lerpT);
		double rightBlend = MathUtility.lerp(rightArcadeValue, rightCurvatureValue, lerpT);

		double[] blends = { leftBlend, rightBlend };
		return blends;
	}

	/**
	 * Drives with a blend between curvature and arcade drive using
	 * linear interpolation
	 *
	 * @param xSpeed the forward/backward speed for the robot
	 * @param zRotation the curvature to drive/the in-place rotation
	 * @see #getBlendedMotorValues(double, double)
	 */
	public void blendedDrive(double desiredSpeed, double rotation, double inputThreshold, boolean speedSmoothing) {
		if (speedSmoothing) {
			double MAX_SPEED_TIME = .5; //change this to change reaction itme!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			double MAX_CHANGE_PER_TICK = 1.0 / ( MAX_SPEED_TIME * 50.0);
			double change = desiredSpeed - currentSpeed;
			if((currentSpeed > 0 && change < 0 ) || (currentSpeed < 0 && change > 0)) {
				if(change > MAX_CHANGE_PER_TICK) {
					change = MAX_CHANGE_PER_TICK;
				} else if (change < -MAX_CHANGE_PER_TICK) {
					change = -MAX_CHANGE_PER_TICK;
				}
			}
			currentSpeed += change;
		} else {
			currentSpeed = desiredSpeed; 
		}
		double[] blendedValues = getBlendedMotorValues(currentSpeed, rotation, inputThreshold);
		SmartDashboard.putNumber("blended values 1", blendedValues[0]);
		SmartDashboard.putNumber("blended values 2", blendedValues[1]);
		robotDrive.tankDrive(blendedValues[0], blendedValues[1]);
	}

	/**
	 * Drives with a blend between curvature and arcade drive using
	 * linear interpolation (this version doesn't use an input threshold)
	 * 
	 * @param xSpeed the forward/backward speed for the robot
	 * @param zRotation the curvature to drive/the in-place rotation
	 */
	public void blendedDrive(double xSpeed, double zRotation, boolean speedSmoothing) {
		blendedDrive(xSpeed, zRotation, INPUT_THRESHOLD, speedSmoothing);
	}

	// TODO(medium): We should move clamp and lerp to a MathUtils class or something. (done) There are now three different subsystems that each have their own definition of clamp, and Robot has its own definition of deadband.
	/**
	 * reverses a deadbanded value
	 * 
	 * @param value value that has been deadbanded 
	 * @param deadband size of deadband
	 * @return undeadbanded value
	 */
	public static double undeadband(double value, double deadband) {
		if (value < 0) {
			double t = -value;
			return MathUtility.lerp(-deadband, -1, t);
		} else if (value > 0) {
			double t = value;
			return MathUtility.lerp(deadband, 1, t);
		} else {
			return 0;
		}
	}

	public void arcadeDrive(double moveValue, double turnValue) {
		robotDrive.arcadeDrive(-moveValue, -turnValue);
	}

	public void tankDrive(double leftSpeed, double rightSpeed) {
		robotDrive.tankDrive(leftSpeed, rightSpeed);
    }
    public static double proportional(double input, double setpoint, double kp) {
		return (setpoint - input) * kp;
	}
	public double getAverageEncoderDistance() {
		//in inches
		return (((rightMaster.getSelectedSensorPosition(0) + leftMaster.getSelectedSensorPosition(0))/2)*TICKS_TO_INCHES);
	}

	public double getHeading() {
		return navx.getAngle();
	}
	
	/**
	 * resets encoder distances & gyro 
	 */
	public void resetTracking() {
		lastEncoderDistanceLeft = 0;
		lastEncoderDistanceRight = 0;
		zeroEncoderLeft = leftMaster.getSelectedSensorPosition(0);
		zeroEncoderRight = rightMaster.getSelectedSensorPosition(0);
		position = new Vector(0, 0);
		navx.reset();
	}

	/**
	 * changes gears for drive train
	 */
	public void toggleGears() {
		gearsSolenoid.set(!gearsSolenoid.get());
	}

	public void setLowGear() {
		gearsSolenoid.set(true);
	}

	public void setHighGear() {
		gearsSolenoid.set(false);
	}

	public void setGear(boolean gear) {
		gearsSolenoid.set(gear);
	}

	public double getLeftDistance() {
		SmartDashboard.putNumber("leftEncoderDistance", leftMaster.getSelectedSensorPosition());
		SmartDashboard.putNumber("leftDistanceInches", leftMaster.getSelectedSensorPosition() * TICKS_TO_INCHES );
		return leftMaster.getSelectedSensorPosition() * TICKS_TO_INCHES;
	}
	public double getRightDistance() {
		SmartDashboard.putNumber("rightEncoderDistance", rightMaster.getSelectedSensorPosition());
		SmartDashboard.putNumber("rightDistanceInches", rightMaster.getSelectedSensorPosition() * TICKS_TO_INCHES );
		return rightMaster.getSelectedSensorPosition() * TICKS_TO_INCHES;
	}

	public Vector getRobotPosition() {
		return position; 
	}

	public void trackLocation() {
		double distanceLeft = getLeftDistance() - lastEncoderDistanceLeft; 
		double distanceRight = getRightDistance() - lastEncoderDistanceRight; 
		double distance = (distanceLeft + distanceRight) / 2; 
		double angle = Math.toRadians(navx.getAngle()); 

		double x = Math.sin(angle) * distance; 
		double y = Math.cos(angle) * distance; 

		Vector changeInPosition = new Vector(x, y); 
		position = position.add(changeInPosition); 

		lastEncoderDistanceLeft = getLeftDistance(); 
		lastEncoderDistanceRight = getRightDistance();
	}
	public static class PurePursuitResult {
		public int indexOfClosestPoint; 
		public int indexOfGoalPoint;
		public Vector goalPoint; 

		public PurePursuitResult(int indexOfClosestPoint, int indexOfGoalPoint, Vector goalPoint) {
			this.indexOfClosestPoint = indexOfClosestPoint; 
			this.indexOfGoalPoint = indexOfGoalPoint;
			this.goalPoint = goalPoint;
		}
	}

	public PurePursuitResult purePursuit(Path pathResult, boolean isBackwards) {
		int indexOfClosestPoint = findClosestPoint(pathResult, position);
		int indexOfGoalPoint = findGoalPoint(pathResult, position, 25);
		Vector goalPoint = pathResult.path[indexOfGoalPoint].subtract(position).rotate(navx.getAngle());
		double angle;
		if(isBackwards) {
			angle = -getAngleToPoint(goalPoint.multiply(-1));
		} else {
			angle = getAngleToPoint(goalPoint);
		}
		double turnValue = purePursuitPID.pid(-angle, 0);
		double speed = DrivingUtility.getTrapezoidSpeed(0.3, 0.75, 0.2, pathResult.path.length, 6, 10, indexOfClosestPoint);

		if(isBackwards) {
			blendedDrive(-speed, -turnValue, false);
		} else {
			blendedDrive(speed, turnValue, false);
		}
		

		logger.info("pure pursiuit intfo",
			new LogField("angle", angle, Logger.SMART_DASHBOARD_TAG),
			new LogField("goalPoint", goalPoint, Logger.SMART_DASHBOARD_TAG),
			new LogField("speed", speed, Logger.SMART_DASHBOARD_TAG),
			new LogField("turn value!", turnValue, Logger.SMART_DASHBOARD_TAG),
			new LogField("closestPoint", indexOfClosestPoint, Logger.SMART_DASHBOARD_TAG),
			new LogField("length of path", pathResult.path.length, Logger.SMART_DASHBOARD_TAG),
			new LogField("actual number of points", pathResult.numberOfActualPoints, Logger.SMART_DASHBOARD_TAG)
		);

		return new PurePursuitResult(indexOfClosestPoint, indexOfGoalPoint, goalPoint);
	}

	public static int findClosestPoint(Path pathResult, Vector fieldPosition) {
		int indexOfClosestPoint = 0;
		double minDistance = pathResult.path[0].subtract(fieldPosition).magnitude(); 
		for(int i = 0; i < pathResult.numberOfActualPoints; i++) {
			double distanceToPoint = pathResult.path[i].subtract(fieldPosition).magnitude();
			if (distanceToPoint <= minDistance) {
				indexOfClosestPoint = i; 
				minDistance = distanceToPoint; 
			}
		}
		return indexOfClosestPoint;
	}

	public static int findGoalPoint(Path pathResult, Vector fieldPosition, int lookAhead) {
		int indexOfClosestPoint = findClosestPoint(pathResult, fieldPosition); 
		return Math.min(indexOfClosestPoint + lookAhead, pathResult.path.length - 1);
	}

	public static double getAngleToPoint(Vector point) {
		if (point.magnitude() == 0) {
			return 0;
		}
		double angle = Math.acos(point.y / point.magnitude());
		return Math.signum(point.x) * Math.toDegrees(angle); 
	}

}

