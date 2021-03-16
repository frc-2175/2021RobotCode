package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.SpeedController;
import frc.ServiceLocator;
import frc.info.RobotInfo;

public class FeederSubsystem {

    private final SpeedController feederMotor;
    RobotInfo robotInfo;

    public FeederSubsystem() {
        robotInfo = ServiceLocator.get(RobotInfo.class);
        feederMotor = robotInfo.pick(() -> new WPI_VictorSPX(3), () -> new WPI_TalonSRX(9));
        ServiceLocator.register(this);
    }

    /**
     * ✩ roll in feeder at speed -1 ✩
     */
    public void rollUp() {
        feederMotor.set(-1);
    }

    /**
     * ✩ roll out feeder at speed 1 ✩
     */
    public void rollDown() {
        feederMotor.set(1);
    }

    public void stopFeeder() {
        feederMotor.set(0);
    }
}