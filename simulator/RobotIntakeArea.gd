extends Area

class_name RobotIntakeArea

export(NodePath) var launcher
onready var _launcher: RobotLauncher = get_node(launcher) as RobotLauncher

func _ready():
	connect("body_entered", self, "_on_body_entered")

func _on_body_entered(body: Node):
	if not (body is RigidBody):
		printerr("A RobotIntakeArea should only detect RigidBody nodes, but detected %s (%s)" % [body.name, body])
		return
	_launcher.store(body)
