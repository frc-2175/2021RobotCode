package frc.command.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.ShooterSubsystem;

public class SpinTurretCommand extends Command {

    private double shootingTime;
    private ShooterSubsystem shooterSubsystem;

    public SpinTurretCommand(double shootingTime) {
        this.shootingTime = shootingTime;
        shooterSubsystem = ServiceLocator.get(ShooterSubsystem.class);
    }

    public void init() {
        
    }

    public void execute() {
        
    }

    public boolean isFinished() {
        return false; // TODO: PROBABLY VERY BAD
    }

    public void end() {
       
    }
}