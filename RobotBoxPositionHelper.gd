tool
extends Spatial

class_name RobotBoxPositionHelper

export(int, "Left", "Center", "Right") var x_position = 1
export(int, "Bottom", "Center", "Top") var y_position = 1
export(int, "Back", "Center", "Front") var z_position = 1

export(NodePath) var box

func _editor_process():
	var _box = get_node(box) if box else get_parent()
	var w = Math.in2m(_box.width_inches)
	var h = Math.in2m(_box.height_inches)
	var d = Math.in2m(_box.depth_inches)
	
	self.global_transform.origin = _box.global_transform.origin + Vector3(
		w/2 * (x_position-1),
		h/2 * (y_position-1),
		d/2 * (z_position-1)
	)

func _process(_delta):
	if Engine.editor_hint:
		return _editor_process()
