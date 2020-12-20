tool
extends CollisionShape

class_name RobotBox

export(String, "Aluminum", "Polycarb", "Steel") var material = "Aluminum"

export(Math.LengthUnit) var unit = Math.LengthUnit.Inches setget set_unit
export(float) var width = 2
export(float) var height = 1
export(float) var depth = 12

export(float, 0.03125, 0.25) var thickness_inches = 0.125
export(bool) var solid = false

func set_unit(new_unit):
	var wm = Math.length2m(width, unit)
	var hm = Math.length2m(height, unit)
	var dm = Math.length2m(depth, unit)
	width = Math.m2length(wm, new_unit)
	height = Math.m2length(hm, new_unit)
	depth = Math.m2length(dm, new_unit)
	
	unit = new_unit
	property_list_changed_notify()

func ensure_children():
	var mesh: MeshInstance = get_node_or_null(@"Mesh")
	if not mesh:
		mesh = MeshInstance.new()
		mesh.mesh = CubeMesh.new()
		mesh.name = "Mesh"
		self.add_child(mesh)
		mesh.owner = get_tree().get_edited_scene_root()

func get_mass_kg() -> float:
	var w = Math.length2m(width, unit)
	var h = Math.length2m(height, unit)
	var d = Math.length2m(depth, unit)
	var volume_m3 = w * h * d
	if not solid:
		var w2 = Math.length2m(width, unit) - Math.in2m(thickness_inches*2)
		var h2 = Math.length2m(height, unit) - Math.in2m(thickness_inches*2)
		var d2 = Math.length2m(depth, unit) - Math.in2m(thickness_inches*2)
		volume_m3 -= w2 * h2 * d2
	var density_kgpm3 = RobotUtil.get_materials()[material].density_kgpm3
	
	return volume_m3 * density_kgpm3

func _ready():
	apply_center_of_mass_to_body()

func _editor_process():
	ensure_children()
	
	var w = Math.length2m(width, unit)
	var h = Math.length2m(height, unit)
	var d = Math.length2m(depth, unit)
	
	if not self.shape:
		self.shape = BoxShape.new()
	RobotUtil.reset_scale(self)
	self.shape.extents = Vector3(w/2, h/2, d/2)
	
	var mesh = $Mesh as MeshInstance
	RobotUtil.reset_translation(mesh)
	RobotUtil.reset_rotation(mesh)
	RobotUtil.reset_children(mesh)
	mesh.scale = Vector3(w/2, h/2, d/2)
	
	apply_mass_to_body()

func _process(_delta):
	if Engine.editor_hint:
		_editor_process()

# Calculate and set the mass of the parent RigidBody. Every box is
# gonna do this every time, but it doesn't really matter.
func apply_mass_to_body():
	# Find containing RigidBody
	var bodyNode: RigidBody = get_parent()
	if not bodyNode:
		printerr("Node ", self.name, " needs to be a child of a RigidBody.")
		return
	
	# Set stuff
	var body: RigidBody = bodyNode
	body.mass = get_mass(body)
	body.can_sleep = false

func apply_center_of_mass_to_body():
	# Find containing RigidBody
	var bodyNode: RigidBody = get_parent()
	if not bodyNode:
		printerr("Node ", self.name, " needs to be a child of a RigidBody.")
		return
	
	var center_of_mass = get_center_of_mass(bodyNode)
	var body_translate = center_of_mass - bodyNode.global_transform.origin

	bodyNode.global_translate(body_translate)
	for child in bodyNode.get_children():
		if child is Spatial:
			child.global_translate(-body_translate)

# Walk through a node's children, adding up the masses
func get_mass(node: RigidBody) -> float:
	var sum: float = 0	
	for child in node.get_children():
		if child.has_method("get_mass_kg"):
			sum += child.get_mass_kg()
	return sum

# Gets a node's center of mass in world coordinates.
func get_center_of_mass(node: RigidBody) -> Vector3:
	var didInitialize: bool = false
	var center: Vector3 = Vector3(0, 0, 0)
	var total_mass: float = 0.0
	
	for child in node.get_children():
		if child.has_method("get_mass_kg"):
			child = child as Spatial
			if not didInitialize:
				# initialize!
				center = child.global_transform.origin
				total_mass = child.get_mass_kg()
				didInitialize = true
			else:
				var new_total_mass = total_mass + child.get_mass_kg()
				center = lerp(center, child.global_transform.origin, 1 - (total_mass / new_total_mass))
				total_mass = new_total_mass
	
	return center
