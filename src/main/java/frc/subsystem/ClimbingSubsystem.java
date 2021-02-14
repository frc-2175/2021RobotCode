package frc.subsystem;

import java.security.Provider.Service;

import frc.MotorWrapper;
import frc.ServiceLocator;
import frc.info.RobotInfo;

public class ClimbingSubsystem{
	private MotorWrapper verticalClimber;
	private MotorWrapper horizontalClimber;

	public ClimbingSubsystem() {
		ServiceLocator.register(this);

		RobotInfo robotInfo = ServiceLocator.get(RobotInfo.class);
		verticalClimber = robotInfo.get(RobotInfo.CLIMBER_VERTICAL_MOTOR);
		horizontalClimber = robotInfo.get(RobotInfo.CLIMBER_VERTICAL_MOTOR);
	}

	public void climbMoveForward() {
		horizontalClimber.set(0.5);
	}
	public void climbMoveBack() {
		horizontalClimber.set(-0.5);
	}
	public void climbMoveUp() {
		verticalClimber.set(1);
	}
	public void climbMoveDown() {
		verticalClimber.set(-1);
	}
	public void climbStop() {
		verticalClimber.set(0.0);
		horizontalClimber.set(0.0);
	}
}


