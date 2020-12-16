tool
extends RigidBody

class_name RobotCylinder

export(String, "Aluminum", "Polycarb", "Steel") var material = "Aluminum"

export(float, 0.25, 2) var radius_inches = 1
export(float, 1, 60) var length_inches = 12

export(float, 0.03125, 0.25) var thickness_inches = 0.125
export(bool) var solid = false

func get_mass_kg():
	var r = Math.in2m(radius_inches)
	var volume_m3 = PI*r*r * Math.in2m(length_inches)
	if not solid:
		var r2 = Math.in2m(radius_inches-thickness_inches*2)
		volume_m3 -= PI*r2*r2 * Math.in2m(length_inches)
	var density_kgpm3 = RobotUtil.get_materials()[material].density_kgpm3
	
	return volume_m3 * density_kgpm3

func ensure_children():
	var mesh = get_node_or_null(@"Mesh")
	if not mesh:
		mesh = MeshInstance.new()
		mesh.mesh = CylinderMesh.new()
		mesh.name = "Mesh"
		self.add_child(mesh)
		mesh.owner = get_tree().get_edited_scene_root()
		
	var collider = get_node_or_null(@"Collider")
	if not collider:
		collider = CollisionShape.new()
		collider.shape = CylinderShape.new()
		collider.name = "Collider"
		self.add_child(collider)
		collider.owner = get_tree().get_edited_scene_root()
	
	var start = get_node_or_null(@"Start")
	if not start:
		start = Spatial.new()
		start.name = "Start"
		self.add_child(start)
		start.owner = get_tree().get_edited_scene_root()
		
	var end = get_node_or_null(@"End")
	if not end:
		end = Spatial.new()
		end.name = "End"
		self.add_child(end)
		end.owner = get_tree().get_edited_scene_root()

func _editor_process():
	ensure_children()
	
	self.can_sleep = false
	
	var r = Math.in2m(radius_inches)
	var h = Math.in2m(length_inches)
	
	var collider = $Collider as CollisionShape
	var shape = collider.shape as CylinderShape
	RobotUtil.reset_translation(collider)
	RobotUtil.reset_rotation(collider)
	RobotUtil.reset_scale(collider)
	RobotUtil.reset_children(collider)
	shape.height = h
	shape.radius = r
	
	var mesh = $Mesh as MeshInstance
	RobotUtil.reset_translation(mesh)
	RobotUtil.reset_rotation(mesh)
	RobotUtil.reset_children(mesh)
	mesh.scale = Vector3(r, h/2, r)
	
	self.mass = get_mass_kg()
	
	var start = $Start as Spatial
	start.translation = Vector3(0, -h/2, 0)
	RobotUtil.reset_rotation(start)
	RobotUtil.reset_scale(start)
	
	var end = $End as Spatial
	end.translation = Vector3(0, h/2, 0)
	RobotUtil.reset_rotation(end)
	RobotUtil.reset_scale(end)

func _process(_delta):
	if Engine.editor_hint:
		return _editor_process()
