tool
extends RigidBody

export(float, 1, 8) var wheelDiameterIn = 5
export(float, 0, 2) var wheelWidthIn = 1

var in2cm = 2.54
var wheelRadiusM: float
var wheelWidthM: float

var dragStrength = 30

enum ControllerType {TalonSRX, VictorSPX}
export(ControllerType) var controller_type
export(int) var motor_id = 1

# see CIM datasheet https://content.vexrobotics.com/docs/217-2000-CIM-motor-specs.pdf
#var maxTorque = 0.4519 # Newton-meters, from 64 oz-in of torque under normal load
var maxTorque = 1.5 # nah screw math

onready var sim: Node = RobotUtil.find_parent_by_script(self, RobotSimClient)

func calculateConstants():
	wheelRadiusM = wheelDiameterIn * in2cm / 100 / 2
	wheelWidthM = wheelWidthIn * in2cm / 100

func _ready():
	calculateConstants()

func _process(_delta):
	if Engine.editor_hint:
		calculateConstants()
		var collider = find_node("CollisionShape") as CollisionShape
		var shape = collider.shape as BoxShape
		shape.extents = Vector3(wheelWidthM/2, wheelRadiusM, wheelRadiusM)

func _physics_process(_delta):
	if Engine.editor_hint:
		return

	var space_state = get_world().direct_space_state
	var result = space_state.intersect_ray(global_transform.origin, global_transform.origin + Vector3(0, -wheelRadiusM - 0.05, 0))
	
	if "normal" in result:
		# add drive force
		var speed = get_speed()
		var torque = speed * maxTorque * transform.basis.x
		var forceDirection = torque.cross(result.normal).normalized()
		var forceMagnitude = torque.length() / wheelRadiusM
		add_central_force(forceDirection * forceMagnitude)
		
		# add lateral drag
		add_central_force(dragStrength * -linear_velocity.project(transform.basis.x))
		
		# add parallel drag (to aggressively halt when stopped)
		if abs(speed) < 0.05:
			add_central_force(dragStrength * -linear_velocity.project(transform.basis.z))

func get_speed() -> float:
	match controller_type:
		ControllerType.TalonSRX:
			return SimTalonSRX.new(sim, motor_id).get_percent_output()
		ControllerType.VictorSPX:
			return SimVictorSPX.new(sim, motor_id).get_percent_output()
		_:
			printerr("Unrecognized controller type in wheel: ", controller_type)
			return 0.0
