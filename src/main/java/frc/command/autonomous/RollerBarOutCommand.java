package frc.command.autonomous;

import edu.wpi.first.wpilibj.Timer;
import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.CargoIntakeSubsystem;

public class RollerBarOutCommand extends Command {
    private CargoIntakeSubsystem cargoIntakeSubsystem;
    private double timeRollingOut;
    private double startTime;
    private double nowTime;

    public RollerBarOutCommand(double timeRollingOut) {
        this.timeRollingOut = timeRollingOut;
        cargoIntakeSubsystem = ServiceLocator.get(CargoIntakeSubsystem.class);
    }

    public void init() {
        startTime = Timer.getFPGATimestamp();
    }

    public void execute() {
        nowTime = Timer.getFPGATimestamp();
        cargoIntakeSubsystem.solenoidOut();
        cargoIntakeSubsystem.rollIn();
    }

    public boolean isFinished() {
        return timeRollingOut <= (nowTime - startTime); //if time has run for as long or more long than we want, stop !!! : )
    }

    public void end() {
        cargoIntakeSubsystem.solenoidIn();
        cargoIntakeSubsystem.stopAllMotors();
    }
}
