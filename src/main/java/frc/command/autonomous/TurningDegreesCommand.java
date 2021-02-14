package frc.command.autonomous;

import frc.command.Command;
import frc.subsystem.DrivetrainSubsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.ServiceLocator;


public class TurningDegreesCommand extends Command {
    double turnDegrees;
    DrivetrainSubsystem driveTrainSubsystem;



    public TurningDegreesCommand(double turnDegrees) {
        this.turnDegrees = turnDegrees;
        driveTrainSubsystem = ServiceLocator.get(DrivetrainSubsystem.class);
    }

    public void init() {
        driveTrainSubsystem.resetTracking();
    }

    public void execute() {
        if (turnDegrees > 0) {
            driveTrainSubsystem.blendedDrive(0, .7);
        } else if (turnDegrees < 0) {
            driveTrainSubsystem.blendedDrive(0, -.7);
        } else {
            driveTrainSubsystem.blendedDrive(0, 0);
        }

    }

    public boolean isFinished() {
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


    }

    public void end() {
        driveTrainSubsystem.blendedDrive(0, 0);
    }

}
