package frc.command.autonomous;

import frc.command.Command;
import frc.subsystem.DrivetrainSubsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.ServiceLocator;


public class DriveStraightCommand extends Command {
    double distance;
    DrivetrainSubsystem driveTrainSubsystem;



    public DriveStraightCommand(double distance) {
        this.distance = distance;
        driveTrainSubsystem = ServiceLocator.get(DrivetrainSubsystem.class);
    }

    public void init() {
        driveTrainSubsystem.resetTracking();
    }

    public void execute() {
        double turnDegrees = getTurnDegrees();
        double turnSpeed;
        if(turnDegrees > 0) {
            turnSpeed = .2;
        } else if (turnDegrees < 0) {
            turnSpeed = -.2;
        } else {
            turnSpeed = 0.0;
        }
        driveTrainSubsystem.blendedDrive(.6, turnSpeed);

       /* if (turnDegrees > 0) {
            driveTrainSubsystem.blendedDrive(.6, .7);
        } else if (turnDegrees < 0) {
            driveTrainSubsystem.blendedDrive(.6, -.7);
        } else {
            driveTrainSubsystem.blendedDrive(.6, 0);
        } */

    }

    public boolean isFinished() {
        if(driveTrainSubsystem.getRightSideDistanceDriven() >= distance) {
            return true;
        } else {
            return false;
        }


    }

    public void end() {
        driveTrainSubsystem.blendedDrive(0, 0);
    }
/*
    public boolean doneTurning() {
        if(turnDegrees > 0) { //if our turn degrees is positive
            if(driveTrainSubsystem.getHeading() >= turnDegrees) {
                return true;
            } else {
                return false;
            }
        } else if (turnDegrees < 0) { //if it's negative ... do the other thing
            if(driveTrainSubsystem.getHeading() <= turnDegrees) {
                return true;
            } else {
                return false;
            }
        } else { //if it's turning 0 degrees
                return true;
        }
    } */

    	/**
	 * 
	 * @return returns degrees to turn so that robot drives straight
	 */
	public double getTurnDegrees() { 
		double safetyAreaDegrees = 1.0; //change this if you want safety area/deadband?? to be a diff num
		double curDegrees = driveTrainSubsystem.getHeading(); //getting current angle of robot (straight on is 0 i think)
		if(curDegrees > safetyAreaDegrees) { // if robot is turned to too much degrees,
			return -curDegrees; //return the num of degrees needed to turn back to zero
		} if (curDegrees < -safetyAreaDegrees) {
			return -curDegrees;
		} else { //if it's perfectly fine within safety zone,
			return 0;//just tell it to go straight :)
		}
	}

}