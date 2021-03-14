package frc.command.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.command.Command;

public class TimerCommand extends Command {

    private double timeToRun;
    private double startTime;
    private double currentTime;

    public TimerCommand(double timeToRun) {
        this.timeToRun = timeToRun;
    }

    public void init() {
        startTime = Timer.getFPGATimestamp();
    }

    public void execute() {
        currentTime = Timer.getFPGATimestamp();
    }

    public boolean isFinished() {
        return (currentTime - startTime) >= timeToRun;
    }

    public void end() {
    }
}

