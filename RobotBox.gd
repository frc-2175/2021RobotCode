tool
extends RigidBody

class_name RobotBox

export(String, "Aluminum", "Polycarb", "Steel") var material = "Aluminum"

export(float, 1, 60) var width_inches = 2
export(float, 1, 60) var height_inches = 1
export(float, 1, 60) var depth_inches = 12

export(float, 0.03125, 0.25) var thickness_inches = 0.125
export(bool) var solid = false

func ensure_children():
	var mesh: MeshInstance = get_node_or_null(@"Mesh")
	if not mesh:
		mesh = MeshInstance.new()
		mesh.mesh = CubeMesh.new()
		mesh.name = "Mesh"
		self.add_child(mesh)
		mesh.owner = get_tree().get_edited_scene_root()
	
	var collider: CollisionShape = get_node_or_null(@"Collider")
	if not collider:
		collider = CollisionShape.new()
		collider.shape = BoxShape.new()
		collider.name = "Collider"
		self.add_child(collider)
		collider.owner = get_tree().get_edited_scene_root()
			

func get_mass_kg():
	var w = Math.in2m(width_inches)
	var h = Math.in2m(height_inches)
	var d = Math.in2m(depth_inches)
	var volume_m3 = w * h * d
	if not solid:
		var w2 = Math.in2m(width_inches-thickness_inches*2)
		var h2 = Math.in2m(height_inches-thickness_inches*2)
		var d2 = Math.in2m(depth_inches-thickness_inches*2)
		volume_m3 -= w2 * h2 * d2
	var density_kgpm3 = RobotUtil.get_materials()[material].density_kgpm3
	
	return volume_m3 * density_kgpm3

func _editor_process():
	ensure_children()
	
	self.can_sleep = false
	
	var width = Math.in2m(width_inches)
	var height = Math.in2m(height_inches)
	var depth = Math.in2m(depth_inches)
	
	var collider = $Collider as CollisionShape
	var shape = collider.shape as BoxShape
	RobotUtil.reset_translation(collider)
	RobotUtil.reset_rotation(collider)
	RobotUtil.reset_scale(collider)
	RobotUtil.reset_children(collider)
	shape.extents = Vector3(width/2, height/2, depth/2)
	
	var mesh = $Mesh as MeshInstance
	RobotUtil.reset_translation(mesh)
	RobotUtil.reset_rotation(mesh)
	RobotUtil.reset_children(mesh)
	mesh.scale = Vector3(width/2, height/2, depth/2)
	
	self.mass = get_mass_kg()

func _process(_delta):
	if Engine.editor_hint:
		_editor_process()
