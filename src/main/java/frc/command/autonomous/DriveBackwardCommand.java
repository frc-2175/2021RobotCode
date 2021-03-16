package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.DrivetrainSubsystem;

public class DriveBackwardCommand extends Command {
    double distance;
    DrivetrainSubsystem drivetrainSubsystem;
    
    public DriveBackwardCommand(double distance) {
        this.distance = distance;
        drivetrainSubsystem = ServiceLocator.get(DrivetrainSubsystem.class);
    }
    public void init() {
        drivetrainSubsystem.resetTracking();
    }
    public void execute() {
        drivetrainSubsystem.tankDrive( -.6, -.6);
    }
    public boolean isFinished() {
        if(drivetrainSubsystem.getAverageEncoderDistance() >= distance) {
            return true;
        } else {
            return false;
        }
    }

    public void end() {
        drivetrainSubsystem.stopAllMotors();

    }
}

