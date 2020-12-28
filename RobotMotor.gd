extends Spatial

class_name RobotMotor

enum ControllerType {TalonFX, TalonSRX, VictorSPX, PWM}
export(ControllerType) var controller_type
export(int) var motor_id = 1

# see CIM datasheet https://content.vexrobotics.com/docs/217-2000-CIM-motor-specs.pdf
var max_torque = 0.4519 # Newton-meters, from 64 oz-in of torque under normal load
#var max_torque = 1.5 # nah screw math

onready var sim: Node = RobotUtil.find_parent_by_script(self, RobotSim)
onready var directly_apply: bool = get_parent() as RigidBody != null

func _physics_process(_delta):
	if directly_apply:
		var speed = sim.get_data(device_type(controller_type), device_id(controller_type, motor_id), speed_prop(controller_type), 0)
		var torque = speed * max_torque * global_transform.basis.x
		var body = get_parent() as RigidBody
		body.add_torque(torque)

func device_type(t):
	match t:
		ControllerType.PWM:
			return "PWM"
		_:
			return "SimDevices"

func device_id(t, id: int):
	match t:
		ControllerType.TalonFX:
			return "Talon FX[%d]" % id
		ControllerType.TalonSRX:
			return "Talon SRX[%d]" % id
		ControllerType.VictorSPX:
			return "Victor SPX[%d]" % id
		_:
			return str(id)

func speed_prop(t):
	match t:
		ControllerType.PWM:
			return "<speed"
		_:
			return "<>Motor Output"
