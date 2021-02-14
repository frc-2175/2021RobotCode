/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.command.autonomous;

import frc.command.Command;
import frc.ServiceLocator;
import frc.subsystem.DrivetrainSubsystem;


/**
 * Add your docs here.
 */
public class DriveForwardInchesCommand extends Command {

    double distance;
    DrivetrainSubsystem driveTrain;

    public DriveForwardInchesCommand(double distance) {
        this.distance = distance;
        driveTrain = ServiceLocator.get(DrivetrainSubsystem.class);
    }

    public void init() {
        driveTrain.resetTracking();
    }

    public void execute() {
        driveTrain.blendedDrive( .6, 0);
    }

    public boolean isFinished() {
        // System.out.println(driveTrain.getRightSideDistanceDriven());

        if(driveTrain.getRightSideDistanceDriven() >= distance) {
            return true;
        } else {
            return false;
        }
    }

    public void end() {
        driveTrain.blendedDrive(0, 0);
    }

}
