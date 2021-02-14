package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.subsystem.HatchIntakeSubsystem;

public class ActuatePanelIntakeOutCommand extends Command {
    private HatchIntakeSubsystem hatchIntakeSubsystem;

    public ActuatePanelIntakeOutCommand() {
        hatchIntakeSubsystem = ServiceLocator.get(HatchIntakeSubsystem.class);
    }

    public void init() {
        hatchIntakeSubsystem.setFrontIntakeOut();
    }

    public void execute() { }

    public boolean isFinished() {
        return true;
    }

    public void end() { }
}
