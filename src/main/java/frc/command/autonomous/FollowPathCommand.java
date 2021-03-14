package frc.command.autonomous;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ServiceLocator;
import frc.command.Command;
import frc.math.DrivingUtility;
import frc.math.DrivingUtility.Path;
import frc.math.DrivingUtility.PathSegment;
import frc.subsystem.DrivetrainSubsystem;

public class FollowPathCommand extends Command {
    DrivetrainSubsystem drivetrainSubsystem = ServiceLocator.get(DrivetrainSubsystem.class);
    Path pathResult;
    DrivetrainSubsystem.PurePursuitResult purePursuitResult; 
    boolean isBackwards;

    PathSegment[] pathSegments;
    public FollowPathCommand(boolean isBackwards, PathSegment... pathSegments) {
        this.pathSegments = pathSegments;
        this.isBackwards = isBackwards;
    }

    public void init() {
        purePursuitResult = null; 
        pathResult = DrivingUtility.makePath(isBackwards, -drivetrainSubsystem.getHeading(), drivetrainSubsystem.getRobotPosition(), pathSegments);
        double[] xCoords = new double[pathResult.path.length];
        double[] yCoords = new double[pathResult.path.length];
        for(int i = 0 ; i < pathResult.path.length; i++) {
            xCoords[i] = pathResult.path[i].x;
            yCoords[i] = pathResult.path[i].y;
        }
        SmartDashboard.putNumberArray("Values/PathXCoords", xCoords);
        SmartDashboard.putNumberArray("Values/PathYCoords", yCoords);
    }

    public void execute() {
        purePursuitResult = drivetrainSubsystem.purePursuit(pathResult, isBackwards);
    }

    public boolean isFinished() {
        if (purePursuitResult == null) {
            return false; 
        }
        if(isBackwards) {
            return purePursuitResult.indexOfClosestPoint == pathResult.numberOfActualPoints - 1 //closest point is last point
            && purePursuitResult.goalPoint.y >= 0; //goal point us behind us
        } else {
            return purePursuitResult.indexOfClosestPoint == pathResult.numberOfActualPoints - 1
            && purePursuitResult.goalPoint.y <= 0;
        }
    }

    public void end() {
        drivetrainSubsystem.stopAllMotors();
    }
}