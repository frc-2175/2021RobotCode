package frc.command;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ServiceLocator;
import frc.subsystem.FeederSubsystem;
import frc.subsystem.MagazineSubsystem;
import frc.subsystem.ShooterSubsystem;

public class AutoFeedCommand extends Command {

    ShooterSubsystem shooterSubsystem = ServiceLocator.get(ShooterSubsystem.class);
    FeederSubsystem feederSubsystem = ServiceLocator.get(FeederSubsystem.class);
    MagazineSubsystem magazineSubsystem = ServiceLocator.get(MagazineSubsystem.class);
    double startTime;
    boolean upToSpeed;
    boolean waiting;

    public AutoFeedCommand() {
        SmartDashboard.putNumber("feeder time", .50);
    }

	@Override
	public void init() {
        waiting = true;
	}

	@Override
	public void execute() {
        double feederTime = SmartDashboard.getNumber("feeder time", .5);
        if ( waiting ) {
            if ( shooterSubsystem.nearTargetSpeed() || getElapsedTime() > 1) {
                waiting = false;
                startTime = Timer.getFPGATimestamp();
            }
        } else {
            if(Timer.getFPGATimestamp() - startTime < feederTime) {
                feederSubsystem.rollUp();
                magazineSubsystem.stopMagazine();
            } else {
                feederSubsystem.rollUp();
                magazineSubsystem.magazineRollIn();
            }
        }
    }
    
	@Override
	public boolean isFinished() {
		return false;
	}

	@Override
	public void end() {
        feederSubsystem.stopFeeder();
        magazineSubsystem.stopMagazine();
	}
}