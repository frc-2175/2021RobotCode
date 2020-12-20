extends Node

class_name RobotUtil

static func get_materials():
	return {
		Aluminum = {
			density_kgpm3 = 2700
		},
		Polycarb = {
			density_kgpm3 = 1360
		},
		Steel = {
			density_kgpm3 = 7859
		}
	}

static func reset_translation(node: Spatial):
	if node.translation != Vector3(0, 0, 0):
		printerr("Don't move the node '%s' directly; move its parent, '%s'." % [node.name, node.get_parent().name])
	node.translation = Vector3(0, 0, 0)

static func reset_rotation(node: Spatial):
	if node.rotation != Vector3(0, 0, 0):
		printerr("Don't rotate the node '%s' directly; rotate its parent, '%s'." % [node.name, node.get_parent().name])
	node.rotation = Vector3(0, 0, 0)

static func reset_scale(node: Spatial):
	if node.scale != Vector3(1, 1, 1):
		printerr("Don't scale the node '%s' directly; scale its parent, '%s'." % [node.name, node.get_parent().name])
	node.scale = Vector3(1, 1, 1)

static func reset_children(node: Node):
	if node.get_child_count() > 0:
		printerr("Don't add children to node '%s'." % node.name)
	for child in node.get_children():
		node.remove_child(child)
		node.get_parent().add_child(child)
		child.owner = node.get_tree().get_edited_scene_root()

static func find_parent_by_script(node: Node, script: Script):
	if not node:
		return null
	if node.get_script() == script:
		return node
	return find_parent_by_script(node.get_parent(), script)
