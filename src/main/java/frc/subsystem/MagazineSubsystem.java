package frc.subsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import frc.ServiceLocator;

public class MagazineSubsystem {

    private final SpeedController magazineMotor;

    public MagazineSubsystem() {
        magazineMotor = new WPI_TalonSRX(7);
        ServiceLocator.register(this);
        
    }

    /**
     * rolls intake in at full in
     */
    public void magazineRollIn() {
        magazineMotor.set(0.87);   
    }
    
    public void stopMagazine() {
        magazineMotor.set(0);
    }

    /**
     * ✩ Actually just sets speed of magazine motor ✩
     * @param speed speed to set magazine motor to
     */
    public void setMagazineMotor(double speed) {
        magazineMotor.set(speed);
    }

    /**
     * rolls intake out at full speed
     */
    public void magazineRollOut() {
        magazineMotor.set(-1);
    }

    
}