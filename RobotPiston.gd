tool
extends Spatial

class_name RobotPiston

export(int) var id = 0
export(float) var bore_diameter_inches = 0.88
export(float) var length_inches = 12

onready var sim: Node = get_node("/root/Spatial")
onready var _piston_base: RigidBody = $Base
onready var _piston_rod: RigidBody = $Rod

var psi = 60
var lbf2n = 4.448
var efficiency = 0.85
var retract_ratio = 0.89

var pressure_area_square_inches = PI*(bore_diameter_inches/2)*(bore_diameter_inches/2)
var force_lbf = psi * pressure_area_square_inches
var force_n = force_lbf * lbf2n * efficiency

func ensure_children():
	var base = get_node_or_null(@"Base")
	if not base:
		base = RobotCylinder.new()
		base.name = "Base"
		self.add_child(base)
		base.owner = get_tree().get_edited_scene_root()
	
	var rod = get_node_or_null(@"Rod")
	if not rod:
		rod = RobotCylinder.new()
		rod.name = "Rod"
		self.add_child(rod)
		rod.owner = get_tree().get_edited_scene_root()
	
	var slider = get_node_or_null(@"Base/SliderJoint")
	if not slider:
		slider = SliderJoint.new()
		slider.name = "SliderJoint"
		base.add_child(slider)
		slider.owner = get_tree().get_edited_scene_root()

func _editor_process():
	ensure_children()

	var base: RobotCylinder = $Base
	base.translation = Vector3(0, Math.in2m(length_inches/2), 0)
	RobotUtil.reset_rotation(base)
	RobotUtil.reset_scale(base)
	base.radius_inches = (bore_diameter_inches/2) + 0.5
	base.length_inches = length_inches
	base.solid = false
	
	var rod: RobotCylinder = $Rod
	rod.translation = Vector3(0, Math.in2m(length_inches/2 + 1), 0)
	RobotUtil.reset_rotation(rod)
	RobotUtil.reset_scale(rod)
	rod.radius_inches = base.radius_inches * 0.5
	rod.length_inches = length_inches + 1
	rod.solid = true
	
	var slider: SliderJoint = $Base/SliderJoint
	slider.translation = Vector3(0, Math.in2m(1), 0)
	slider.rotation = Vector3(0, 0, deg2rad(90))
	RobotUtil.reset_scale(slider)
	RobotUtil.reset_children(slider)
	slider.set_node_a(@"../../Base")
	slider.set_node_b(@"../../Rod")
	slider.set_param(SliderJoint.PARAM_LINEAR_LIMIT_UPPER, 0.15)
	slider.set_param(SliderJoint.PARAM_LINEAR_LIMIT_LOWER, 0)
	slider._set_upper_limit_angular(180)
	slider._set_lower_limit_angular(-180)

func _process(_delta):
	if Engine.editor_hint:
		return _editor_process()

func _physics_process(_delta):
	if Engine.editor_hint:
		return

	var on = sim.get_data("PCM", str(0), "<solenoid_output_" + str(id), false)
	var sgn = 1 if on else -1
	var retract_reduction = 1 if on else retract_ratio
	
	var force = force_n * retract_reduction
	
	_piston_base.add_central_force(-sgn * _piston_base.global_transform.basis.y * force)
	_piston_rod.add_central_force(sgn * _piston_base.global_transform.basis.y * force)
