extends Camera

export(NodePath) var target

onready var target_node = get_node(target) as Spatial

func _process(_delta):
	if target_node:
		look_at(target_node.global_transform.origin, Vector3.UP)
