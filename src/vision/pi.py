#!/usr/bin/env python3

import json
import time
import sys

if __name__ == "__main__":
    from cscore import CameraServer, VideoSource, UsbCamera, MjpegServer
    from networktables import NetworkTables, NetworkTablesInstance
    import ntcore

import cv2
import numpy
import math

hueThreshold = (63.129496402877685, 103.5993208828523) # min/max
saturationThreshold = (43.57014388489208, 255.0)
valueThreshold = (91.72661870503595, 255.0)
minArea = 200

# Processes an image and returns the following:
# final image to view, NetworkTables keys/values
def process(img):
    img = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    img = cv2.inRange(img, (hueThreshold[0], saturationThreshold[0], valueThreshold[0]),  (hueThreshold[1], saturationThreshold[1], valueThreshold[1]))
    _, contours, _ = cv2.findContours(img, mode = cv2.RETR_EXTERNAL, method = cv2.CHAIN_APPROX_SIMPLE)
    
    biggestContour = None
    maxAreaSoFar = 0
    for contour in contours: 
        area = cv2.contourArea(contour)
        if area >= minArea:
            if area > maxAreaSoFar:
                maxAreaSoFar = area
                biggestContour = contour
    if biggestContour is None:
        return img, {}
    biggestContour = cv2.convexHull(biggestContour)
    smallestX = (100000000, 0)
    biggestX = (-100000000, 0)

    contourPoints = []
    for n in biggestContour : 
        contourPoints.append(n[0])
    for p in contourPoints :
        if p[0] < smallestX[0] :
            smallestX = (p[0], p[1])
        if p[0] > biggestX[0] :
            biggestX = (p[0], p[1])

    print("smallest: " + str(smallestX) + ", biggest: " + str(biggestX))

    shapeImage = numpy.zeros((img.shape[0], img.shape[1], 3), dtype=numpy.uint8)
    blue = abs( math.sin(time.time()) * 255 )
    yellow = abs( math.cos(time.time()) * 255 )
    red = abs( math.sin(time.time()) * 255 )
    shapeImage = cv2.drawContours(shapeImage, [biggestContour], -1,(blue,yellow,red), cv2.FILLED)
    shapeImage = cv2.drawMarker(shapeImage, smallestX, (255,255,255))
    shapeImage = cv2.drawMarker(shapeImage, biggestX, (255,255,255))
    return shapeImage, {
        'test1': 3,``
        'test2': True,
    }

# ---------------------------------------------------------------------------
# DO NOT EDIT! This is stuff from the example program that we need so that
# our program can properly load the camera config from the web UI.
# 
# You can edit again starting at the message saying OK NOW YOU CAN EDIT.
# ---------------------------------------------------------------------------

#----------------------------------------------------------------------------
# Copyright (c) 2018 FIRST. All Rights Reserved.
# Open Source Software - may be modified and shared by FRC teams. The code
# must be accompanied by the FIRST BSD license file in the root directory of
# the project.
#----------------------------------------------------------------------------

configFile = "/boot/frc.json"

class CameraConfig: pass

team = None
server = False
cameraConfigs = []
switchedCameraConfigs = []
cameras = []

def parseError(str):
    """Report parse error."""
    print("config error in '" + configFile + "': " + str, file=sys.stderr)

def readCameraConfig(config):
    """Read single camera configuration."""
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read camera name")
        return False

    # path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("camera '{}': could not read path".format(cam.name))
        return False

    # stream properties
    cam.streamConfig = config.get("stream")

    cam.config = config

    cameraConfigs.append(cam)
    return True

def readSwitchedCameraConfig(config):
    """Read single switched camera configuration."""
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read switched camera name")
        return False

    # path
    try:
        cam.key = config["key"]
    except KeyError:
        parseError("switched camera '{}': could not read key".format(cam.name))
        return False

    switchedCameraConfigs.append(cam)
    return True

def readConfig():
    """Read configuration file."""
    global team
    global server

    # parse file
    try:
        with open(configFile, "rt", encoding="utf-8") as f:
            j = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
        return False

    # top level must be an object
    if not isinstance(j, dict):
        parseError("must be JSON object")
        return False

    # team number
    try:
        team = j["team"]
    except KeyError:
        parseError("could not read team number")
        return False

    # ntmode (optional)
    if "ntmode" in j:
        str = j["ntmode"]
        if str.lower() == "client":
            server = False
        elif str.lower() == "server":
            server = True
        else:
            parseError("could not understand ntmode value '{}'".format(str))

    # cameras
    try:
        cameras = j["cameras"]
    except KeyError:
        parseError("could not read cameras")
        return False
    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    # switched cameras
    if "switched cameras" in j:
        for camera in j["switched cameras"]:
            if not readSwitchedCameraConfig(camera):
                return False

    return True

def startCamera(config):
    """Start running the camera."""
    print("Starting camera '{}' on {}".format(config.name, config.path))
    inst = CameraServer.getInstance()
    camera = UsbCamera(config.name, config.path)
    server = inst.startAutomaticCapture(camera=camera, return_server=True)

    camera.setConfigJson(json.dumps(config.config))
    camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen)

    if config.streamConfig is not None:
        server.setConfigJson(json.dumps(config.streamConfig))

    return camera

def startSwitchedCamera(config):
    """Start running the switched camera."""
    print("Starting switched camera '{}' on {}".format(config.name, config.key))
    server = CameraServer.getInstance().addSwitchedCamera(config.name)

    def listener(fromobj, key, value, isNew):
        if isinstance(value, float):
            i = int(value)
            if i >= 0 and i < len(cameras):
              server.setSource(cameras[i])
        elif isinstance(value, str):
            for i in range(len(cameraConfigs)):
                if value == cameraConfigs[i].name:
                    server.setSource(cameras[i])
                    break

    NetworkTablesInstance.getDefault().getEntry(config.key).addListener(
        listener,
        ntcore.constants.NT_NOTIFY_IMMEDIATE |
        ntcore.constants.NT_NOTIFY_NEW |
        ntcore.constants.NT_NOTIFY_UPDATE)

    return server

if __name__ == "__main__":
    if len(sys.argv) >= 2:
        configFile = sys.argv[1]

    # read configuration
    if not readConfig():
        sys.exit(1)

    # start NetworkTables
    ntinst = NetworkTablesInstance.getDefault()
    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        ntinst.startClientTeam(team)
        
    # assume we are sending:
    # cornerX - array clockwise from top left
    # cornerY - array clockwise from top left

    # start cameras
    for config in cameraConfigs:
        cameras.append(startCamera(config))

    # start switched cameras
    for config in switchedCameraConfigs:
        startSwitchedCamera(config)

    # ------------------------------------------------------
    # OK NOW YOU CAN EDIT AGAIN
    # ------------------------------------------------------
        
    cs = CameraServer.getInstance()
    sink = cs.getVideo(camera=cameras[0])
    outputStream = cs.putVideo("Processed rPi Video", 320, 240)
    img = numpy.zeros(shape=(320, 240, 3), dtype=numpy.uint8)
    
    sd = NetworkTables.getTable("SmartDashboard")
    i = 0
    
    # loop forever
    while True:
        time, img = sink.grabFrame(img)
        if time == 0:
            outputStream.notifyError(sink.getError())
            continue
        
        img, values = process(img)
        for name, value in values.items():
            sd.putValue(name, value)
        
        outputStream.putFrame(img)
