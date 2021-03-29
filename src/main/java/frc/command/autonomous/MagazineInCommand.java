// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.MagazineSubsystem;

/** Add your docs here. */
public class MagazineInCommand extends Command {

    private MagazineSubsystem magazineSubsystem;

    public MagazineInCommand(){
        magazineSubsystem = ServiceLocator.get(MagazineSubsystem.class);
    }

    @Override
    public void init() {

    }

    @Override
    public void execute() {
        magazineSubsystem.magazineRollIn();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end() {
        magazineSubsystem.stopMagazine();

    }
}
