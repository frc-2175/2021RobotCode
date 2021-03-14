package frc.command;

import edu.wpi.first.wpilibj.Timer;
import frc.spacetime.SpacetimeEvent;

public abstract class Command {
	protected SpacetimeEvent event;
	private double startTime;

    /**
     * Runs on the start of the command.
     */
    public abstract void init();

    /**
     * Runs periodically after the init call.
     */
    public abstract void execute();

    /**
     * @return whether or not the command is finished
     */
    public abstract boolean isFinished();

    /**
     * Runs after the command ends.
     */
	public abstract void end();

	public void _init() {
		if(event != null) {
			event.start();
		}
		startTime = Timer.getFPGATimestamp();
		init();
	}

	public void _execute() {
		execute();
	}

	public boolean _isFinished() {
		return isFinished();
	}

	public void _end() {
		if(event != null) {
			event.end();
		}

		end();
	}

	public void initSpacetimeEvent(SpacetimeEvent parentEvent) {
		event = parentEvent.makeChild(this.getClass().getName());
	}

	protected double getElapsedTime() {
		return Timer.getFPGATimestamp() - startTime;
	}
}