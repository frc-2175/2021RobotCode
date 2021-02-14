package frc.subsystem;

import frc.MotorWrapper;
import frc.ServiceLocator;
import frc.SolenoidWrapper;
import frc.info.RobotInfo;
import frc.info.SmartDashboardInfo;

public class CargoIntakeSubsystem {
    private final MotorWrapper rollerBarMotor; //big grey
    private final MotorWrapper boxMotor;//small red
	private final SolenoidWrapper rollerBarSolenoid;
	private final SmartDashboardInfo smartDashboardInfo;

    public CargoIntakeSubsystem() {
        ServiceLocator.register(this);
        RobotInfo robotInfo = ServiceLocator.get(RobotInfo.class);
        rollerBarMotor = robotInfo.get(RobotInfo.CARGO_ROLLER_BAR_MOTOR);
        boxMotor = robotInfo.get(RobotInfo.CARGO_BOX_MOTOR);
		rollerBarSolenoid = robotInfo.get(RobotInfo.CARGO_SOLENOID);
		smartDashboardInfo = ServiceLocator.get(SmartDashboardInfo.class);
    }
    public void rollIn() { //cargo in
        rollerBarMotor.set(smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_INTAKE_ROLL_IN_ROLLERBAR_SPEED));
        boxMotor.set(smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_INTAKE_ROLL_IN_BOX_MOTOR_SPEED));
    }
    public void rollOut() { //cargo out
        rollerBarMotor.set(smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_INTAKE_ROLL_OUT_ROLLERBAR_SPEED));
        boxMotor.set(smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_INTAKE_ROLL_OUT_BOX_MOTOR_SPEED));
    }
    
    public void rollJustBoxOut() {
        boxMotor.set(smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_INTAKE_ROLL_OUT_BOX_MOTOR_SPEED));
    }

    public void rollJustRollerBarOut() {
        rollerBarMotor.set(smartDashboardInfo.getNumber(SmartDashboardInfo.CARGO_INTAKE_ROLL_OUT_ROLLERBAR_SPEED));
    }

    public void rollJustRollerBarOutSlow() {
        rollerBarMotor.set(-0.5);
    }

    public void solenoidOut() { // push out the solenoid, cargo roller out, Y
        rollerBarSolenoid.set(true);
    }
    public void solenoidIn() { // pull in the solenoid, cargo roller in, A
        rollerBarSolenoid.set(false);
	}

	public void stopAllMotors() {
		boxMotor.set(0);
		rollerBarMotor.set(0);
    }
    
    public void spinRollerbarForElevator() {
        rollerBarMotor.set(0.4);
    }
}
