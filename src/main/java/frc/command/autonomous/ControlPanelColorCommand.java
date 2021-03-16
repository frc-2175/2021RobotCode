package frc.command.autonomous;

import frc.ServiceLocator;
import frc.command.Command;
import frc.math.MathUtility;
import frc.subsystem.ControlPanelSubsystem;

public class ControlPanelColorCommand extends Command {

    private ControlPanelSubsystem controlPanelSubsystem;
    private String goalColor;
    private String currentColor;
    private static double[] redAngles = { 22.5, 202.5 };
    private static double[] greenAngles = { 67.5, 247.5 };
    private static double[] blueAngles = { 112.5, 292.5 };
    private static double[] yellowAngles = { 157.5, 337.5 };

    public ControlPanelColorCommand(String goalColor) {
        controlPanelSubsystem = ServiceLocator.get(ControlPanelSubsystem.class);
        this.goalColor = goalColor;
    }

    public void init() {

    }

    public void execute() {
        currentColor = ControlPanelSubsystem
            .getControlPanelColor(ControlPanelSubsystem.getHue(controlPanelSubsystem.getColorSensorRed(),
            controlPanelSubsystem.getColorSensorGreen(), controlPanelSubsystem.getColorSensorBlue()));
        double shortestDistance = getShortestDistance(currentColor, goalColor);
        if (shortestDistance > 0) {
            controlPanelSubsystem.spinControlPanelForward();
        } else {
            controlPanelSubsystem.spinControlPanelBackward();
        }
    }

    public boolean isFinished() { //remember that your current color is not the one under the pointer !!
        if (currentColor.equals("red")) {
            return goalColor.equals("blue");
        } else if (currentColor.equals("green")) {
            return goalColor.equals("yellow");
        } else if (currentColor.equals("blue")) {
            return goalColor.equals("red");
        } else if (currentColor.equals("yellow")) {
            return goalColor.equals("green");
        } else {
            return false; 
        }

    }

    public void end() {
        controlPanelSubsystem.stopSpinControlPanel();

    }

    public static double getShortestDistance(String currentColor, String goalColor) {
        double currentAngle;
        if (currentColor.equals("red")) {
            currentAngle = blueAngles[0];
        } else if (currentColor.equals("green")) {
            currentAngle = yellowAngles[0];
        } else if (currentColor.equals("blue")) {
            currentAngle = redAngles[0];
        } else if (currentColor.equals("yellow")) {
            currentAngle = greenAngles[0];
        } else {
            currentAngle = 0;
        }

        double[] goalAngles;
        if (goalColor.equals("red")) {
            goalAngles = redAngles;
        } else if (goalColor.equals("green")) {
            goalAngles = greenAngles;
        } else if (goalColor.equals("blue")) {
            goalAngles = blueAngles;
        } else if (goalColor.equals("yellow")) {
            goalAngles = yellowAngles;
        } else {
            goalAngles = new double[] { 0, 0 };
        }
        double distance1 = MathUtility.getDistanceBetweenAngles(currentAngle, goalAngles[0]);
        double distance2 = MathUtility.getDistanceBetweenAngles(currentAngle, goalAngles[1]);

        double shortestDistance;
        if (Math.abs(distance1) < Math.abs(distance2)) {
            shortestDistance = distance1;
        } else {
            shortestDistance = distance2;
        }
        return shortestDistance;
    }
}
