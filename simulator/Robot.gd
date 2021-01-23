extends Spatial

class_name Robot

export(bool) var start_with_max_air = false

var system_air_volume_cm3: float
var extra_air_volume_cm3: float = 0.0

var atmospheric_pressure_Npcm2 = 10.13

func _ready():
	system_air_volume_cm3 = get_system_air_volume_cm3()
	if start_with_max_air:
		extra_air_volume_cm3 = pressure_Npcm2_to_extra_air_volume_cm3(Math.psi2Npcm2(120) + atmospheric_pressure_Npcm2)

func get_volume_scale(extra_cm3: float = extra_air_volume_cm3):
	if system_air_volume_cm3 == 0 or extra_cm3 == 0:
		return 1.0

	return system_air_volume_cm3 / (system_air_volume_cm3 + extra_cm3)

func get_air_pressure_Npcm2(extra_cm3: float = extra_air_volume_cm3) -> float:
	if system_air_volume_cm3 == 0:
		return 0.0

	return atmospheric_pressure_Npcm2 / get_volume_scale(extra_cm3)

func get_working_pressure_Npcm2() -> float:
	return min(Math.psi2Npcm2(60), get_air_pressure_Npcm2())

func get_system_air_volume_cm3(node: Node = null) -> float:
	if not node:
		node = self
	
	var total: float = 0.0
	if node.has_method("get_air_volume_cm3"):
		total += node.get_air_volume_cm3()
	for child in node.get_children():
		total += get_system_air_volume_cm3(child)
	
	return total

# I dunno man, I just did some algebra and this came out
func pressure_Npcm2_to_extra_air_volume_cm3(pressure_Npcm2: float) -> float:
	return ((pressure_Npcm2 * system_air_volume_cm3) / atmospheric_pressure_Npcm2) - system_air_volume_cm3

# awkwardly fit to this data:
# https://www.andymark.com/products/air-compressor?via=Z2lkOi8vYW5keW1hcmsvV29ya2FyZWE6Ok5hdmlnYXRpb246OlNlYXJjaFJlc3VsdHMvJTdCJTIycSUyMiUzQSUyMmNvbXByZXNzb3IlMjIlN0Q
# using this desmos:
# https://www.desmos.com/calculator/iwmfznaobj
func compressor_flow_rate_cfm(psi: float) -> float:
	return (0.7 / (0.15 * psi + 1)) + 0.22

func vent_working_air_cm3(volume_cm3):
	var adjusted_volume = volume_cm3 / get_volume_scale()
	extra_air_volume_cm3 = max(0, extra_air_volume_cm3 - adjusted_volume)

func _process(delta):
	var current_pressure_psi = Math.Npcm22psi(get_air_pressure_Npcm2() - atmospheric_pressure_Npcm2)
	if current_pressure_psi < 120:
		var current_flow_rate_cfm = compressor_flow_rate_cfm(current_pressure_psi)
		var current_flow_rate_cm3ps = Math.cfm2cm3ps(current_flow_rate_cfm)
		var new_air_cm3 = current_flow_rate_cm3ps * delta
		extra_air_volume_cm3 += new_air_cm3
