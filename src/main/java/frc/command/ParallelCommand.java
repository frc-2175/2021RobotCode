package frc.command;

import frc.spacetime.SpacetimeEvent;

/**
 * Runs commands in parallel (at the same time). This command will end when the
 * last of the commands in parallel has ended.
 */
public class ParallelCommand extends Command {
    protected final Command[] commands;
    protected boolean[] hasEndRunYet;

    /**
     * @param commands an array of the commands to be run in parallel
     */
    public ParallelCommand(Command... commands) {
        this.commands = commands;
        System.out.println("# Of Commands");
        System.out.println(commands.length);
        hasEndRunYet = new boolean[commands.length];
    }

    public void init() {
        for (Command command : commands) {
            command._init();
        }
    }

    public void execute() {
        for(int i = 0; i < commands.length; i++) {
			if(!hasEndRunYet[i]) {
				commands[i]._execute();
			}
			if(!hasEndRunYet[i] && commands[i]._isFinished()) {
                System.out.println("Ending a command (parallel)");
                commands[i]._end();
                hasEndRunYet[i] = true;
            }
        }
    }

    public boolean isFinished() {
        boolean isFinished = true;
        for (Command command : commands) {
            if(!command._isFinished()) {
                isFinished = false;
            }
        }
        return isFinished;
    }

    public void end() {
        for(int i = 0; i < commands.length; i++) {
            if(!hasEndRunYet[i]) {
                System.out.println("Ending a command");
                commands[i]._end();
            }
			hasEndRunYet[i] = false;
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