package frc.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.PIDController;
import frc.ServiceLocator;

public class ShooterSubsystem {
    
    private final CANSparkMax shooterMotorMaster;
    private final CANSparkMax shooterMotorFollower;
    private final WPI_TalonSRX turretMotor;
    private final PIDController turretPidController;
    private Solenoid hoodPiston;

    private SpeedController hoodMotor;
    public enum Mode {Manual, PID, BangBang};
    private Mode currentMode = Mode.Manual;
    private double goalSpeedRPM;
    private double manualSpeed = 0;
    private double targetSpeed;
    private double goalAngle;
    private static final double OVERSHOOTNESS = 100; //the amount to add to the target speed in bbspeed calculations!!!!!
    private static final double MAX_RPM = 5620;
    private static final double BUFFER_ZONE = 100;
    private static final double TURRET_OUTPUT_GEAR_TEETH = 200.0; 
    private static final double TURRET_INPUT_GEAR_TEETH = 30.0; 
    private static final double TURRET_TICKS_TO_DEGREES = 360.0/4096.0 * (TURRET_INPUT_GEAR_TEETH / TURRET_OUTPUT_GEAR_TEETH);
    private enum HoodPosition {Forward, Backward}; 
    private HoodPosition desiredHoodPosition;
    private HoodPosition actualHoodPosition;
    private static final double MAX_TURRET_ROTATION_DEGREES = 200; 
    private static final double INITIAL_TURRET_ROTATION_DEGREES = 30; 

    public ShooterSubsystem() {
        shooterMotorMaster = new CANSparkMax(1, MotorType.kBrushless);
        shooterMotorFollower = new CANSparkMax(2, MotorType.kBrushless);
        turretMotor = new WPI_TalonSRX(2); //not accurate
        hoodMotor = new WPI_VictorSPX(99); // get value!!
        hoodPiston = new Solenoid(3); 

        double ti = 0;
        double tp = 0.5/90.0;
        double td = 0;

        SmartDashboard.putNumber("turretpid i", ti);
        SmartDashboard.putNumber("turretpid p", tp);
        SmartDashboard.putNumber("turretpid d", td);

        
        SmartDashboard.putNumber("speed goal()rpm??", 4500);
        SmartDashboard.putNumber("overshootness", OVERSHOOTNESS);
        SmartDashboard.putNumber("turret encoder", turretMotor.getSelectedSensorPosition());

        turretPidController = new PIDController(tp, ti, td);
        shooterMotorMaster.setIdleMode(IdleMode.kCoast);
        shooterMotorFollower.setIdleMode(IdleMode.kCoast);
        shooterMotorMaster.setInverted(true);
        turretMotor.setInverted(true);
        // shooterMotorFollower.setInverted(false);
        shooterMotorFollower.follow(shooterMotorMaster, true);
        turretMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 0);
        ServiceLocator.register(this);
    }

    public void updateTurretPIDConstants() {
        turretPidController.kp = SmartDashboard.getNumber("turretpid p", 0);
        turretPidController.ki = SmartDashboard.getNumber("turretpid i", 0);
        turretPidController.kd = SmartDashboard.getNumber("turretpid d", 0);
    }

    public void periodic() {
        SmartDashboard.putNumber("flywheel speed (rpm)", getSpeedInRPM());
        SmartDashboard.putNumber("current turrent angle", getCurrentTurretAngle());
        turretPidController.updateTime(Timer.getFPGATimestamp());
        if (currentMode == Mode.PID) { //PID!!
            CANPIDController noah2 = shooterMotorMaster.getPIDController();
            noah2.setP(.000006);
            noah2.setI(0);
            noah2.setD(0);
            noah2.setIZone(0);
            noah2.setReference(SmartDashboard.getNumber("shooter rpm", 60), ControlType.kVelocity);
        } else if (currentMode == Mode.BangBang) { //Bang bang !!!!!
            if ( getSpeedInRPM() >  (targetSpeed)) {
                shooterMotorMaster.set(0);
            } else if ( getSpeedInRPM() < (targetSpeed)) {
                shooterMotorMaster.set((targetSpeed + OVERSHOOTNESS) / MAX_RPM);
            } 
        } else { //do manual stuff!!!!!!!!
            shooterMotorMaster.set(manualSpeed);
        }
        if (turretMotor.get() != 0) {
            actualHoodPosition = HoodPosition.Forward; 
        } else {
            actualHoodPosition = desiredHoodPosition; 
        }
        if (actualHoodPosition == HoodPosition.Forward) {
            hoodPiston.set(true);
        } else {
            hoodPiston.set(false);
        }
    }

    public double getSpeedInRPM() {
        return shooterMotorMaster.getEncoder().getVelocity();
    }

    public void setMode(Mode sesh) {
        currentMode = sesh;
    }

    public void setHoodMotor(double speed) {
        hoodMotor.set(speed);
    }

    public void toggleHoodAngle() {
        if(hoodPiston.get()) {
            desiredHoodPosition = HoodPosition.Backward;
        } else {
            desiredHoodPosition = HoodPosition.Forward;
        }
    }

    /**
     * set manual mode speed for shooter flywheel
     * @param jacob speed
     */
    public void setManualSpeed(double jacob) {
        manualSpeed = jacob;
    }

    public void setTurretSpeed(double speed) {
        turretMotor.set(speed);
    }

    public void turretPIDToGoalAngle() {
        double currentAngle = turretTicksToDegrees(turretMotor.getSelectedSensorPosition());
        double output = turretPidController.pid(currentAngle, goalAngle);
        setTurretSpeed(output);
    }

    public void setGoalAngle(double relativeGoalAngle) {
        goalAngle = relativeGoalAngle + turretTicksToDegrees(turretMotor.getSelectedSensorPosition());
    }


    public void clearTurretPID() {
        turretPidController.clear(Timer.getFPGATimestamp());
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    public void setTargetSpeed(double speed) {
        targetSpeed = speed;
    }

    /**
     * 
     * @return are we within a certain range of our goal speed
     */
    public boolean nearTargetSpeed() {
        return targetSpeed - BUFFER_ZONE <= getSpeedInRPM() && getSpeedInRPM() <= targetSpeed + BUFFER_ZONE;
    }
    
    public static double turretTicksToDegrees(double ticks) {
        return TURRET_TICKS_TO_DEGREES * ticks;
    }

    public void zeroTurretPosition() {
        turretMotor.setSelectedSensorPosition(0); 
    }

    public double getAngleFromGoalAngle() {
        return Math.abs(getCurrentTurretAngle() - goalAngle); 
    }

    public double getCurrentTurretAngle() {
        return turretTicksToDegrees(turretMotor.getSelectedSensorPosition()); 
    }
 
}


    //2 motors - on button A
    //shoot out method