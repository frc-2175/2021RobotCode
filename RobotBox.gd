tool
extends CollisionShape

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

func get_mass_kg() -> float:
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
	
	var width = Math.in2m(width_inches)
	var height = Math.in2m(height_inches)
	var depth = Math.in2m(depth_inches)
	
	if not self.shape:
		self.shape = BoxShape.new()
	RobotUtil.reset_scale(self)
	self.shape.extents = Vector3(width/2, height/2, depth/2)
	
	var mesh = $Mesh as MeshInstance
	RobotUtil.reset_translation(mesh)
	RobotUtil.reset_rotation(mesh)
	RobotUtil.reset_children(mesh)
	mesh.scale = Vector3(width/2, height/2, depth/2)
	
	apply_mass_to_body()

func _process(_delta):
	if Engine.editor_hint:
		_editor_process()

# Calculate and set the mass of the parent RigidBody. Every box is
# gonna do this every time, but it doesn't really matter.
func apply_mass_to_body():
	# Find containing RigidBody
	var bodyNode: Node = get_parent()
	while bodyNode:
		if bodyNode is RigidBody:
			break
		else:
			bodyNode = bodyNode.get_parent()
	
	if not bodyNode:
		printerr("Node ", self.name, " needs to be inside a RigidBody.")
		return
	
	# Set stuff
	var body: RigidBody = bodyNode
	body.mass = get_masses(body)
	body.can_sleep = false
	
# Recursively walk through a node and its children, adding up the masses
func get_masses(node: Node) -> float:
	var sum: float = 0	
	if node.has_method("get_mass_kg"):
		sum += node.get_mass_kg()
	for child in node.get_children():
		sum += get_masses(child)
	
	return sum
