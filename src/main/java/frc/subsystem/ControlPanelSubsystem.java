package frc.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import frc.ServiceLocator;
import frc.math.MathUtility;

public class ControlPanelSubsystem {

    public final WPI_TalonSRX controlPanelMotor;
    private final ColorSensorV3 colorSensor; 
    public static double redHue = 0;
    public static double yellowHue = 60;
    public static double greenHue = 120;
    public static double cyanHue = 180;


    public ControlPanelSubsystem() {
        ServiceLocator.register(this);
        controlPanelMotor = new WPI_TalonSRX(10);
        colorSensor = new ColorSensorV3(I2C.Port.kOnboard);
        controlPanelMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
    }

    public void spinControlPanelForward() {
        controlPanelMotor.set(0.5);
    }

    public void spinControlPanelBackward() {
      controlPanelMotor.set(-0.5);
  }

    public void stopSpinControlPanel() {
        controlPanelMotor.set(0);
    }

    public double getColorSensorRed() {
        return colorSensor.getColor().red;
    } 

    public double getColorSensorBlue() {
        return colorSensor.getColor().blue;
    }

    public double getColorSensorGreen() {
        return colorSensor.getColor().green;
    }

    public double getColorSensorIR() {
        return colorSensor.getIR();
    }

    public double getColorSensorProximity() {
        return colorSensor.getProximity();
    }

    public static String getControlPanelColor(double hue) {
      double redDistance = Math.abs(MathUtility.getDistanceBetweenAngles(hue, redHue));
      double yellowDistance = Math.abs(MathUtility.getDistanceBetweenAngles(hue, yellowHue));
      double greenDistance = Math.abs(MathUtility.getDistanceBetweenAngles(hue, greenHue));
      double cyanDistance = Math.abs(MathUtility.getDistanceBetweenAngles(hue, cyanHue));
      double lowestDistance = Math.min(Math.min(redDistance, yellowDistance), Math.min(greenDistance, cyanDistance));
      System.out.println("rd: " + redDistance + " yd: " + yellowDistance + " gd: " + greenDistance + " ld: " + lowestDistance);
      if (lowestDistance == redDistance) {
        return "red";
      } else if (lowestDistance == yellowDistance) {
        return "yellow";
      } else if (lowestDistance == greenDistance) {
        return "green";
      } else if (lowestDistance == cyanDistance) {
        return "cyan";
      } else {
        return "";
      }
    }

    public static double getHue(double red, double green, double blue) {
        double cMax = Math.max(Math.max(red, green), blue);
        double cMin = Math.min(Math.min(red, green), blue);
        double delta = cMax - cMin;
        System.out.println("cMax = " + cMax + " cMin = " + cMin + " delta = " + delta);
    
        if (delta == 0) {
          System.out.println("0");
          return 0;
        } else if (cMax == red) {
          System.out.println("red");
          return 60 * MathUtility.mod(((green - blue) / delta), 6);
        } else if (cMax == green) {
          System.out.println("green");
          return 60 * (((blue - red) / delta) + 2);
        } else if (cMax == blue) {
          System.out.println("blue");
          return 60 * (((red - green) / delta) + 4);
        } else {
          return 0;
        }
      } 

}