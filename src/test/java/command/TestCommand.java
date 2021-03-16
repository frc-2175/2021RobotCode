package command;

import frc.command.Command;

public class TestCommand extends Command {
	private int timesExecuted = 0;
	private boolean hasInitialized = false;
	private int timesEnded = 0;
	private boolean _isFinished = false;

	public void init() {
		hasInitialized = true;
	}

	public void execute() {
		timesExecuted++;
	}

	public boolean isFinished() {
		return _isFinished;
	}

	public void end() {
		timesEnded++;
	}

	public int getTimes() {
		return timesExecuted;
	}

	public boolean getHasInitalized() {
		return hasInitialized;
	}

	public boolean getHasEnded() {
		return timesEnded > 0;
	}

	public int getTimesEnded() {
		return timesEnded;
	}

	public void setIsFinished(boolean isFinished) {
		this._isFinished = isFinished;
	}
}
