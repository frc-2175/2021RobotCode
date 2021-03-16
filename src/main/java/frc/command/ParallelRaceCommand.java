package frc.command;

public class ParallelRaceCommand extends ParallelCommand {
    public ParallelRaceCommand(Command... commands) {
        super(commands);
    }

    @Override
    public boolean isFinished() {
        if (commands.length == 0) {
            return true;
        }

        boolean isFinished = false;
        for (Command command : commands) {
            if (command._isFinished()) {
                isFinished = true;
            }
        }

        return isFinished;
    }
}
