package frc.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.ServiceLocator;
import frc.info.RobotInfo;


public class ClimberSubsystem {

    private final RobotInfo robotInfo;
    private final WPI_TalonSRX deployMotor;
    private final WPI_VictorSPX winchMotor;
    public static final double CLIMBER_MAX_EXTENSION_ROTATIONS = 7.6595744;

    public ClimberSubsystem() {
        ServiceLocator.register(this);
        robotInfo = ServiceLocator.get(RobotInfo.class);
        deployMotor = new WPI_TalonSRX(6);
        deployMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        winchMotor = new WPI_VictorSPX(5);
        deployMotor.setNeutralMode(NeutralMode.Brake);
        deployMotor.setSelectedSensorPosition(0);
        deployMotor.setInverted(true);
        winchMotor.setInverted(true);
    }

    public void deployUp() {
        deployMotor.set(1.0);
    } 

    public void deployDown() {
        deployMotor.set(-.5);
    }

    public void stopDeploy() {
        deployMotor.set(0);
    }

    /**
     * use winch to climb up !!!!
     */
    public void climbUp() {
        winchMotor.set(1);
    }

    /**
     * use winch to climb down !!!!
     */
    public void climbDown() {
        winchMotor.set(-1);
    }

    public void stopClimbing() {
        winchMotor.set(0);
    }

    public void periodic() {
        SmartDashboard.putNumber("ClimberEncoder", deployMotor.getSelectedSensorPosition() / 4096.0);
    }
} 