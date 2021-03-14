package frc.command.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.IntakeSubsystem;

public class IntakeCommand extends Command {

    private double timeToSpinIn;
    private IntakeSubsystem intakeSubsystem;
    private double startTime;
    private double currentTime;

    public IntakeCommand(double timeToSpinIn) {
        this.timeToSpinIn = timeToSpinIn;
        intakeSubsystem = ServiceLocator.get(IntakeSubsystem.class);
    }
    public void init() {
        startTime = Timer.getFPGATimestamp();
    }

    public void execute() {
        currentTime = Timer.getFPGATimestamp();
        intakeSubsystem.intakeRollOut();
    }

    public boolean isFinished() {
        return (currentTime - startTime) >= timeToSpinIn;
    }

    public void end() {
        intakeSubsystem.stopIntake();
    }
}