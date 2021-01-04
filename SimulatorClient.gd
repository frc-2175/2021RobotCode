extends Node

class_name RobotSim

var devices = {}

export var websocket_url = "ws://127.0.0.1:8080/wpilibws"
var _client = WebSocketClient.new()

func _ready():
	_client.connect("server_close_request", self, "_close_requested")
	_client.connect("connection_closed", self, "_close")
	_client.connect("connection_error", self, "_close")
	_client.connect("connection_established", self, "_connected")
	
	_client.connect("data_received", self, "_on_data")

	var err = _client.connect_to_url(websocket_url)
	print(err)
	if err != OK:
		print("Unable to connect")
		set_process(false)
	
func _close_requested(code, reason):
	print("Server requested close: ", code, reason)

func _close(was_clean = false):
	print("Closed, clean: ", was_clean)
	set_process(false)

func _connected(proto = ""):
	print("Connected with protocol: ", proto)
	_client.get_peer(1).set_write_mode(WebSocketPeer.WRITE_MODE_TEXT)

func _on_data():
	var json = _client.get_peer(1).get_packet().get_string_from_utf8()
	var p = JSON.parse(json)
	if p.error != OK:
		print("Malformed data: ", json)
		return
	
	var type = p.result["type"]
	var id = p.result["device"]
	var data = p.result["data"]
	
	if not type in devices:
		devices[type] = {}
	
	if not id in devices[type]:
		devices[type][id] = {}
	
	for key in data:
		devices[type][id][key] = data[key]

#	if "Talon" in id:
#		print(json)
#
#	if "DI" in type:
#		print(json)

#	if type == "SimDevices":
#		print(json)
	
#	match p.result["type"]:
#		"Joystick", "DriverStation", "DIO", "PCM", "dPWM", "AI", "AO", "PWM", "Encoder", "RoboRIO":
#			pass
#		_:
#			print(json)
	
#	print("Got data from server: ", )

func get_data(type, id, field, default):
	if not type in devices:
		return default
	if not id in devices[type]:
		return default
	if not field in devices[type][id]:
		return default
	return devices[type][id][field]

func send_data(type, id, fields):
	if not _client.get_peer(1).is_connected_to_host():
		return

	var msg = {
		"type": type,
		"device": id,
		"data": fields,
	}
	_client.get_peer(1).put_packet(JSON.print(msg).to_utf8())

func _process(_delta):
	_client.poll()
