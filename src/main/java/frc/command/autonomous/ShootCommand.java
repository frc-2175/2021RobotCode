package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.ShooterSubsystem;
import frc.subsystem.ShooterSubsystem.Mode;

public class ShootCommand extends Command {

    private double shootingTime;
    private ShooterSubsystem shooterSubsystem;
    private double targetSpeed;

    public ShootCommand(double shootingTime, double targetSpeed) {
        this.shootingTime = shootingTime;
        this.targetSpeed = targetSpeed;
        shooterSubsystem = ServiceLocator.get(ShooterSubsystem.class);
    }

    public void init() {
        shooterSubsystem.setMode(Mode.BangBang);
    }

    public void execute() {
        shooterSubsystem.setTargetSpeed(targetSpeed);
    }

    public boolean isFinished() {
        if (getElapsedTime() > shootingTime) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void end() {
        shooterSubsystem.setTargetSpeed(0);
    }
}