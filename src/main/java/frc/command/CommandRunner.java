package frc.command;

import frc.ServiceLocator;
import frc.spacetime.SpacetimeEvent;
import frc.logging.Logger;

public class CommandRunner {
    private Command command;
    private boolean hasRunInit;
	private boolean hasRunEnd;

    public CommandRunner(Command command) {
		this.command = command;
		Logger robotLogger = ServiceLocator.get(Logger.class);
		command.initSpacetimeEvent(new SpacetimeEvent("CommandRunner", robotLogger.newWithExtraFields()));
        hasRunInit = false;
        hasRunEnd = false;
    }

    public void runCommand() {
        if(!hasRunInit) {
            command._init();
            hasRunInit = true;
            if(command._isFinished()) {
                command._end();
                hasRunEnd = true;
            } else {
                command._execute();
            }
        } else if(!hasRunEnd) {
            if(command._isFinished()) {
                command._end();
                hasRunEnd = true;
            } else {
                command._execute();
            }
        }
    }

    public void endCommand() {
        if (!hasRunEnd) {
            command._end();
            hasRunEnd = true;
        }
    }

    public void resetCommand() {
        hasRunInit = false;
        hasRunEnd = false;
    }
}
