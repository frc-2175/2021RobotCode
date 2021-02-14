/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.DrivetrainSubsystem;

/**
 * Add your docs here.
 */
public class TurningRight extends Command{

    DrivetrainSubsystem driveTrain = ServiceLocator.get(DrivetrainSubsystem.class);

    public void init() {
        driveTrain.resetTracking();
    }

    public void execute() {
        driveTrain.blendedDrive(0, .8);
    }

    public  boolean isFinished() {
        if(driveTrain.getHeading() >= 90) {
            return true;
        } else {
            return false;
        }
    }

    public void end() {
        driveTrain.blendedDrive(0, 0);
    }
}
