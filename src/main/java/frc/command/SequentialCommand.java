package frc.command;

import frc.ServiceLocator;
import frc.spacetime.SpacetimeEvent;
import frc.logging.LogField;
import frc.logging.Logger;

/**
 * Runs commands sequentially (one after another). This command will end when
 * the last command ends.
 */
public class SequentialCommand extends Command {
    private final Command[] commands;
    private int index = 0;
	private Logger logger;

    /**
     * @param commands an array of the commands to be run sequentially
     */
    public SequentialCommand(Command... commands) {
        this.commands = commands;
		logger = ServiceLocator.get(Logger.class).newWithExtraFields(new LogField("CommandType", "Sequential"));
    }

    public void init() {
		index = 0;
		if(commands.length != 0) {
			commands[index]._init();
		}
    }

    public void execute() {
        if(commands.length != 0) {
			// Execute
			commands[index]._execute();

			// Check isFinished + transition
			if(commands[index]._isFinished()) {
                logger.debug("Ending a sub-command", new LogField("CommandName", commands[index].getClass().getName()));
                commands[index]._end();
                if(index < commands.length - 1) {
					index += 1;
					logger.debug("Starting a sub-command", new LogField("CommandName", commands[index].getClass().getName()));
					commands[index]._init();
                }
            }
        }
    }

    public boolean isFinished() {
        boolean isFinished = index == commands.length - 1 && commands[commands.length - 1]._isFinished();
        return (commands.length == 0) ? true : isFinished;
    }

    public void end() {
        if(commands.length != 0) {
            commands[index]._end();
        }
	}

	@Override
	public void initSpacetimeEvent(SpacetimeEvent parentEvent) {
		super.initSpacetimeEvent(parentEvent);
		for(Command command : commands) {
			command.initSpacetimeEvent(event);
		}
	}
}