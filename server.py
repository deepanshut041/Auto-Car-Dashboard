import os
from flask import Flask, render_template ,request ,send_from_directory
from threading import Timer
# import serial
from flask_socketio import SocketIO
import time



# -------------------Database Setup----------------------------------------
#
# from database_setup import Base,Restaurant,MenuItem
# from sqlalchemy import create_engine
# from sqlalchemy.orm import sessionmaker

# Connecting date base to server
# engine=create_engine('sqlite:///restaurant.db')
# Base.metadata.bind=engine

# DBSession=sessionmaker(bind=engine)
# session =DBSession()
#
# -------------------------------------------------------------------------

app=Flask(__name__) 
# wrapping our app in SocketIO
socketio = SocketIO(app)

root = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app", "static", "dist")
#ser = serial.Serial('/dev/ttyACM0', 9600)
thread = None;
k = 0;
car_lat = 28.678808
car_lon = 77.498741
car_id = 'Aubf760'
car_type = 'normal'
car_status = 'ok'
#----------------------------------------------------------------------------------------
#This code is for taking lat and lon from serial monitor 
# def set_interval(update_location, sec):
#     def func_wrapper():
#         set_interval(update_location, sec) 
#         update_location()  
#     t = threading.Timer(sec, func_wrapper)
#     t.start()
#     return t
class InfiniteTimer():
    """A Timer class that does not stop, unless you want it to."""

    def __init__(self, seconds, target):
        self._should_continue = False
        self.is_running = False
        self.seconds = seconds
        self.target = target
        self.thread = None

    def _handle_target(self):
        self.is_running = True
        self.target()
        self.is_running = False
        self._start_timer()

    def _start_timer(self):
        if self._should_continue: # Code could have been running when cancel was called.
            self.thread = Timer(self.seconds, self._handle_target)
            self.thread.start()

    def start(self):
        if not self._should_continue and not self.is_running:
            self._should_continue = True
            self._start_timer()
        else:
            print("Timer already started or running, please wait if you're restarting.")

    def cancel(self):
        if self.thread is not None:
            self._should_continue = False # Just in case thread is running and cancel fails.
            self.thread.cancel()
        else:
            print("Timer never started or failed to initialize.")



def update_location():
	global car_lat, car_lon, k, thread
	# read_serial = ser.readline()
	# car_lat = float(read_serial[0:9])
	# car_lon = float(read_serial[10:19])
	print(car_lat,car_lon,k,"          ")
	socketio.emit('location',{
	 	'lat': car_lat,
	 	'lon': car_lon,
	 	'car_status': car_status,
	 	'car_type': car_type
	 	}, namespace = "/socket/location")
	#
	#print(read_serial)
#-----------------------------------------------------------------------------------------

#///////////////////////////////////////////////////////////////////////////
# Simple flask routes for front end application

@app.route('/<path:path>', methods=['GET'])
def static_proxy(path):
    return send_from_directory(root, path)

@app.route('/', methods=['GET'])
def redirect_to_index():
	return send_from_directory(root, 'index.html')

@app.route('/login', methods=['GET'])
def redirect_to_login():
	# set_interval(func, 1)
	return send_from_directory(root, 'index.html')

#/////////////////////////////////////////////////////////////////////////////

# +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
# SocketIo routes
@socketio.on('connect', namespace='/socket/location')
def start_journey():
	global thread
	thread = InfiniteTimer(1, update_location)
	thread.start()
@socketio.on('disconnect', namespace="/socket/location")
def end_journey():
	global thread
	thread.cancel();
	for i in range(0,100,1):
		print("Journey ended")
	os.system("sudo shutdown -h now")
	#@reboot /usr/bin/python /path/to/myFile.py
#+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


if __name__ == '__main__':
	app.debug = True
	socketio.run(app,host = '0.0.0.0', port=5000)


