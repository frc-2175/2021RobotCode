package frc.command.autonomous;

import frc.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ServiceLocator;
import frc.subsystem.ShooterSubsystem;
import frc.subsystem.VisionSubsystem;

public class AimTurretWithVisionCommand extends Command {
    private ShooterSubsystem shooterSubsystem; 
    private VisionSubsystem visionSubsystem; 

    public AimTurretWithVisionCommand() {
        shooterSubsystem = ServiceLocator.get(ShooterSubsystem.class); 
        visionSubsystem = ServiceLocator.get(VisionSubsystem.class);
    }

    public void init() {
        shooterSubsystem.setGoalAngle(visionSubsystem.getLimelightHorizontalOffset());
        SmartDashboard.putNumber("turret goal angle", visionSubsystem.getLimelightHorizontalOffset()); 
    }

    public void execute() {
        shooterSubsystem.turretPIDToGoalAngle();
    }

    public boolean isFinished() {
        SmartDashboard.putNumber("angle from goal angle", shooterSubsystem.getAngleFromGoalAngle()); 
       return shooterSubsystem.getAngleFromGoalAngle() < 1; 
    }

    public void end() {
        visionSubsystem.turnLimelightOff();
        shooterSubsystem.setTurretSpeed(0);
    }

}