extends Node

class_name Math

static func in2cm(_in):
	return _in * 2.54

static func in2ft(_in):
	return _in / 12

static func cm2m(cm):
	return cm * 0.01

static func in2m(_in):
	return cm2m(in2cm(_in))

static func m2in(m):
	return m * 39.3701

static func m2ft(m):
	return in2ft(m2in(m))

static func psi2Nm2(psi):
	return psi * 6895

static func kg2lb(kg):
	return kg * 2.2046

static func lb2kg(lb):
	return lb * 0.4536
