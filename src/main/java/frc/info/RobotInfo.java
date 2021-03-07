package frc.info;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.Filesystem;
import frc.MotorWrapper;
import frc.ServiceLocator;
import frc.SolenoidWrapper;

public class RobotInfo {

	public static final String LEFT_MOTOR_MASTER = "drivetrain.motor.left.master";
	public static final String LEFT_MOTOR_FOLLOWER = "drivetrain.motor.left.follower";
	public static final String RIGHT_MOTOR_MASTER = "drivetrain.motor.right.master";
	public static final String RIGHT_MOTOR_FOLLOWER = "drivetrain.motor.right.follower";
	public static final String CARGO_ROLLER_BAR_MOTOR = "intake.cargo.motor.rollerbar";
	public static final String CARGO_BOX_MOTOR = "intake.cargo.motor.box";
	public static final String CARGO_SOLENOID = "intake.cargo.solenoid";
	public static final String SWAN = "intake.hatch.motor.swan";
	public static final String HATCH_ACTUATOR_SOLENOID = "intake.hatch.solenoid.actuator";
	public static final String ELEVATOR_MOTOR_MASTER = "elevator.motor.master";
	public static final String ELEVATOR_MOTOR_FOLLOWER = "elevator.motor.follower";
	public static final String HATCH_SOLENOID = "intake.hatch.solenoid";
	public static final String CLIMBER_VERTICAL_MOTOR = "climb.motor.vertical";
	public static final String CLIMBER_HORIZONTAL_MOTOR = "climb.motor.horizontal";
	public static interface ValueContainer {
		public Object get();
	}

    private static final String CAN_T_CONTINUE_MSG = "; can't continue";
    private final boolean isComp;
    private HashMap<String, Object> info;

    public RobotInfo() {
		ServiceLocator.register(this);
        Properties properties = loadProperties(Filesystem.getDeployDirectory() + "/bot.properties");
        isComp = Boolean.parseBoolean((String) properties.get("isComp"));
        info = new HashMap<>();
        populate();
    }

    /**
     * This is where all of the information is put into the hash map.
     * @see frc.info.RobotInfo#put(String, Object)
     * */
    public void populate() {
		// On practice, this should be inverted
		put(CARGO_ROLLER_BAR_MOTOR, victor(new WPI_VictorSPX(4), false), victor(new WPI_VictorSPX(4), false));
		put(CARGO_BOX_MOTOR, victor(new WPI_VictorSPX(6)));
		put(LEFT_MOTOR_FOLLOWER, victor(new WPI_VictorSPX(5)));
		put(LEFT_MOTOR_MASTER, talon(new WPI_TalonSRX(9)));
		put(RIGHT_MOTOR_FOLLOWER, victor(new WPI_VictorSPX(1)));
		put(RIGHT_MOTOR_MASTER, talon(new WPI_TalonSRX(8)));
		put(ELEVATOR_MOTOR_FOLLOWER, victor(new WPI_VictorSPX(2), true));
		put(ELEVATOR_MOTOR_MASTER, talon(new WPI_TalonSRX(10)));
		put(SWAN, victor(new WPI_VictorSPX(7)));
		put(CARGO_SOLENOID, () -> new SolenoidWrapper(2, 3), () -> new SolenoidWrapper(3, 4));
		put(HATCH_ACTUATOR_SOLENOID, () -> new SolenoidWrapper(4), () -> new SolenoidWrapper(5));
		put(CLIMBER_VERTICAL_MOTOR, talon(new WPI_TalonSRX(11)));
		put(CLIMBER_HORIZONTAL_MOTOR, victor(new WPI_VictorSPX(3)));
	}

	private MotorWrapper talon(WPI_TalonSRX talon) {
		return new MotorWrapper(talon);
	}

	private MotorWrapper victor(WPI_VictorSPX victor) {
		return new MotorWrapper(victor);
	}

	private MotorWrapper talon(WPI_TalonSRX talon, boolean inverted) {
		return new MotorWrapper(talon, inverted);
	}

	private MotorWrapper victor(WPI_VictorSPX victor, boolean inverted) {
		return new MotorWrapper(victor, inverted);
	}
    /**
     * Puts an object in the hash map
     * @param key the key by which the object is referred to
     * @param comp will be put in the hash map if the robot the code is
     * being run on is the competition robot
     * @param practice will be put in the has map if the robot the code is
     * being run on is the practice robot
     */
    private void put(String key, Object comp, Object practice) {
		Object choice = isComp ? comp : practice;
		info.put(key, choice);
	}
    /**
     * Puts an object in the hash map
     * @param key the key by which the object is referred to
     * @param value the object to put into the hash map (regardless of
     * competition/practice robot)
     */
	private void put(String key, Object value) {
		info.put(key, value);
    }

    /**
     * Puts an object in the hash map. This is used for solenoids to
     * make sure only one solenoid is initialized.
     * <p>Format: put(KEY_VARIABLE, () -> WhateverSolenoidThing(port1, port2),
     * () -> WhateverSolenoidThing(port1, port2))
     * @param key the key by which the object is referred to
     * @param comp
     * @param practice
     */
    private void put(String key, ValueContainer comp, ValueContainer practice) {
		Object choice = isComp ? comp.get() : practice.get();
		info.put(key, choice);
	}

	// TODO(low): Should there be a version of put that take just a single ValueContainer? Technically you could always just use the version that takes a single object, but it might be nice to have consistency.

    /**
     * Gets an object from the hash map
     * @param key the key which refers to the object
     * @param <T> the class of the object. Is usually implicitly set when initializing.
     * @return the object from the hash map
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
		return (T) info.get(key);
	}

    // Properties Loading Code... not much reason to change this

	/**
	 * Load the properties from the specified file name.
	 *
	 * @param fileName The file name, including any desired path (absolute or
	 *        relative).
	 * @return Properties instance loaded with the properties in the file.
	 */
	public static Properties loadProperties(final String fileName) {
		final File file = new File(fileName);
		return loadProperties(file);
	}

	public static Properties loadProperties(final File file) {
		final InputStream inputStream = openPropertiesFile(file);
		final Properties prop = loadPropertiesFromFile(file, inputStream);
		errorIfNoPropertiesLoaded(file, prop);

		return prop;
	}

	private static InputStream openPropertiesFile(final File file) {
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
		} catch (final FileNotFoundException e) {
			final String msg = "Error finding properties file=" + file + CAN_T_CONTINUE_MSG;
			throw new IllegalStateException(msg, e);
		}
		return inputStream;
	}

	private static Properties loadPropertiesFromFile(final File file, final InputStream inputStream) {
		final Properties prop = new Properties();
		try {
			prop.load(inputStream);
		} catch (final IOException e) {
			final String msg = "Error reading properties file=" + file + CAN_T_CONTINUE_MSG;
			throw new IllegalStateException(msg, e);
		}
		return prop;
	}

	private static void errorIfNoPropertiesLoaded(final File file, final Properties prop) {
		if (prop.isEmpty()) {
			final String msg = "No properties were loaded from file=" + file + CAN_T_CONTINUE_MSG;
			throw new IllegalStateException(msg);
		}
	}
}
