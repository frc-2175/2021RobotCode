package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.ServiceLocator;

public class MagazineSubsystem {

    private final SpeedController magazineMotorLeft;
    private final SpeedController magazineMotorRight;

    public MagazineSubsystem() {
        magazineMotorLeft = new WPI_TalonSRX(7);
        magazineMotorRight = new WPI_TalonSRX(6);

        magazineMotorRight.setInverted(true);

        ServiceLocator.register(this);
        
    }

    /**
     * rolls intake in at full in
     */
    public void magazineRollIn() {
        double speed = 0.87;
        magazineMotorLeft.set(speed);
        magazineMotorRight.set(speed);
    }
    
    public void stopMagazine() {
        magazineMotorLeft.set(0);
        magazineMotorRight.set(0);
    }

    /**
     * ✩ Actually just sets speed of magazine motor ✩
     * @param speed speed to set magazine motor to
     */
    public void setMagazineMotor(double speed) {
        magazineMotorLeft.set(speed);
        magazineMotorRight.set(speed);
    }

    /**
     * rolls intake out at full speed
     */
    public void magazineRollOut() {
        magazineMotorLeft.set(-1);
        magazineMotorRight.set(-1);
    }

    
}