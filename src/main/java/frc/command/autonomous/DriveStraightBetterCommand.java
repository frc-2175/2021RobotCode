package frc.command.autonomous;

import frc.command.Command;
import frc.subsystem.DrivetrainSubsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.ServiceLocator;


public class DriveStraightBetterCommand extends Command {
    double distance;
    double n; //magnitude/absolute max turn speed
    DrivetrainSubsystem driveTrainSubsystem;



    public DriveStraightBetterCommand(double distance, double n) {
        this.distance = distance;
        this.n = n;
        driveTrainSubsystem = ServiceLocator.get(DrivetrainSubsystem.class);
    }

    public void init() {
        driveTrainSubsystem.resetTracking();
    }

    public void execute() {
        double turnDegrees = getTurnDegrees(); 
        double turnSpeed;
        turnSpeed = (turnDegrees/90) * n;
        // GO KARINA WOOOO !!!!!!
        /*if (turnDegrees < 0) { // if ur degrees are negative
            if (turnDegrees > -90) { //and they're greater than negative 90
                turnSpeed = (turnDegrees/90) * n; //your turn speed will be a fraction of max speed
            } else { 
                turnSpeed = n; //just turn the full max speed 
            }
        } else if (turnDegrees > 0) { //or else if it's greater than zero 
            if (turnDegrees < 90) { //and it's smaller than 90
                turnSpeed = (turnDegrees/90) * n; //turn speed is negative fraction of max speed
            } else { 
                turnSpeed = -n; //turn negative max speed ..?
            }
        } else {
            turnSpeed = 0; //if it's none of those go 0 
        } */
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
		if(curDegrees > safetyAreaDegrees || curDegrees < -safetyAreaDegrees) { // if robot is turned to too much degrees,
			return -curDegrees; //return the num of degrees needed to turn back to zero
		} else { //if it's perfectly fine within safety zone,
			return 0;//just tell it to go straight :)
		}
	}

}