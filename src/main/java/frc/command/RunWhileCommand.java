package frc.command;

import frc.spacetime.SpacetimeEvent;

public class RunWhileCommand extends Command {
	// While the first command is running, the second command will also run. 
	// When the first command is finished, the second command will stop as well : )

	private Command primaryCommand;
	private Command secondaryCommand;

	private boolean hasSecondaryEnded;

	public RunWhileCommand(Command primary, Command secondary) {
		this.primaryCommand = primary;
		this.secondaryCommand = secondary;
	}

	public void init() {
		primaryCommand._init();
		secondaryCommand._init();
	}

	public void execute() {
		primaryCommand._execute();
		if (!hasSecondaryEnded) {
			secondaryCommand._execute();

			if (secondaryCommand._isFinished()) {
				endSecondaryCommand();
			}
		}
	}

	public boolean isFinished() {
		return primaryCommand._isFinished();
	}

	public void end() {
		primaryCommand._end();
		if (!hasSecondaryEnded) {
			endSecondaryCommand();
		}
	}

	@Override
	public void initSpacetimeEvent(SpacetimeEvent parentEvent) {
		super.initSpacetimeEvent(parentEvent);
		primaryCommand.initSpacetimeEvent(event);
		secondaryCommand.initSpacetimeEvent(event);
	}

	private void endSecondaryCommand() {
		secondaryCommand._end();
		hasSecondaryEnded = true;
	}
}