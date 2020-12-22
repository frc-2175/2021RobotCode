tool
extends Spatial

class_name RobotPiston

export(int) var id = 0
export(float) var bore_diameter_inches = 0.88
export(float) var length_inches = 12

onready var robot: Robot = RobotUtil.find_parent_by_script(self, Robot) as Robot
onready var sim: Node = RobotUtil.find_parent_by_script(self, RobotSim)
onready var _piston_base: RigidBody = $Base
onready var _piston_rod: RigidBody = $Rod

var lbf2n = 4.448
var efficiency = 0.85
var retract_ratio = 0.89

func get_air_volume_cm3():
	var bore_area_cm2 = PI * Math.in2cm(bore_diameter_inches/2) * Math.in2cm(bore_diameter_inches/2)
	return bore_area_cm2 * Math.in2cm(length_inches - 1 ) # Sub 1 for plunger. Not really accurate but whatever?

func ensure_children():
	var base = get_node_or_null(@"Base")
	if not base:
		base = RigidBody.new()
		base.name = "Base"
		self.add_child(base)
		base.owner = get_tree().get_edited_scene_root()
	
	var base_cylinder = get_node_or_null(@"Base/Cylinder")
	if not base_cylinder:
		base_cylinder = RobotCylinder.new()
		base_cylinder.name = "Cylinder"
		base.add_child(base_cylinder)
		base_cylinder.owner = get_tree().get_edited_scene_root()
	
	var slider = get_node_or_null(@"Base/SliderJoint")
	if not slider:
		slider = SliderJoint.new()
		slider.name = "SliderJoint"
		base.add_child(slider)
		slider.owner = get_tree().get_edited_scene_root()
	
	var rod = get_node_or_null(@"Rod")
	if not rod:
		rod = RigidBody.new()
		rod.name = "Rod"
		self.add_child(rod)
		rod.owner = get_tree().get_edited_scene_root()

	var rod_cylinder = get_node_or_null(@"Rod/Cylinder")
	if not rod_cylinder:
		rod_cylinder = RobotCylinder.new()
		rod_cylinder.name = "Cylinder"
		rod.add_child(rod_cylinder)
		rod_cylinder.owner = get_tree().get_edited_scene_root()

func _editor_process():
	ensure_children()

	var base: RigidBody = $Base
	base.translation = Vector3(0, Math.in2m(length_inches/2), 0)
	RobotUtil.reset_rotation(base)
	RobotUtil.reset_scale(base)
	
	var base_cylinder: RobotCylinder = $Base/Cylinder
	RobotUtil.reset_translation(base_cylinder)
	RobotUtil.reset_rotation(base_cylinder)
	RobotUtil.reset_scale(base_cylinder)
	base_cylinder.radius_inches = (bore_diameter_inches/2) + 0.5
	base_cylinder.length_inches = length_inches
	base_cylinder.solid = false
	
	var rod: RigidBody = $Rod
	rod.translation = Vector3(0, Math.in2m(length_inches/2 + 1), 0)
	RobotUtil.reset_rotation(rod)
	RobotUtil.reset_scale(rod)
	
	var rod_cylinder: RobotCylinder = $Rod/Cylinder
	RobotUtil.reset_translation(rod_cylinder)
	RobotUtil.reset_rotation(rod_cylinder)
	RobotUtil.reset_scale(rod_cylinder)
	rod_cylinder.radius_inches = base_cylinder.radius_inches * 0.5
	rod_cylinder.length_inches = length_inches + 1
	rod_cylinder.solid = true
	
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

var last_on: bool = false

func _physics_process(_delta):
	if Engine.editor_hint:
		return

	var on = sim.get_data("PCM", str(0), "<solenoid_output_" + str(id), false)
	var sgn = 1 if on else -1
	var retract_reduction = 1 if on else retract_ratio
	
	if on != last_on:
		robot.vent_working_air_cm3(get_air_volume_cm3())
	
	var psi = Math.Npcm22psi(robot.get_working_pressure_Npcm2() - robot.atmospheric_pressure_Npcm2)
	var pressure_area_square_inches = PI*(bore_diameter_inches/2)*(bore_diameter_inches/2)
	var force_lbf = psi * pressure_area_square_inches
	var force_n = force_lbf * lbf2n * efficiency
	var force = force_n * retract_reduction
	
	_piston_base.add_central_force(-sgn * _piston_base.global_transform.basis.y * force)
	_piston_rod.add_central_force(sgn * _piston_base.global_transform.basis.y * force)
	
	last_on = on
