package frc.command.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.IntakeSubsystem;

public class OuttakeCommand extends Command {

    private double timeToSpinOut;
    private IntakeSubsystem intakeSubsystem;
    private double startTime;
    private double currentTime;

    public OuttakeCommand(double timeToSpinOut) {
        this.timeToSpinOut = timeToSpinOut;
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
        return (currentTime - startTime) >= timeToSpinOut;
    }

    public void end() {
        intakeSubsystem.stopIntake();
    }
}

