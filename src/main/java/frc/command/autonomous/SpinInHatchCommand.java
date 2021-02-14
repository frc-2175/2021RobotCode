package frc.command.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.HatchIntakeSubsystem;

public class SpinInHatchCommand extends Command {
    HatchIntakeSubsystem hatchIntakeSubsystem;
    double timeToSpin;
    double startTime;
    double nowTime;

    public SpinInHatchCommand(double timeToSpin) {
        this.timeToSpin = timeToSpin;
        hatchIntakeSubsystem = ServiceLocator.get(HatchIntakeSubsystem.class);
    }

    public void init() {
        startTime = Timer.getFPGATimestamp();
    }

    public void execute() {
        hatchIntakeSubsystem.spinInFront();
    }

    public boolean isFinished() {
        nowTime = Timer.getFPGATimestamp();
        return startTime - nowTime >= timeToSpin;
    }

    public void end() {
        hatchIntakeSubsystem.stopSpinning();
    }

}