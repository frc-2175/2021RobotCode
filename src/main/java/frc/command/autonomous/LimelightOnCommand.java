package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.VisionSubsystem;

public class LimelightOnCommand extends Command {
    private VisionSubsystem visionSubsystem; 
    private double delayTime; 
    
    public LimelightOnCommand(double delayTime) {
        visionSubsystem = ServiceLocator.get(VisionSubsystem.class); 
        this.delayTime = delayTime; 
    }

    public void init() {
        visionSubsystem.turnLimelightOn();
    }

    public void execute() {

    }

    public boolean isFinished() {
        if (getElapsedTime() > delayTime) {
            System.out.println("delay finished"); 
            return true;
        } else {
            return false; 
        }
    }
    public void end() {

    }
}