import os
from flask import Flask, render_template ,request ,send_from_directory
import threading
import serial
from flask_socketio import SocketIO




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
ser = serial.Serial('/dev/ttyACM0', 9600)
lat = 51.678418
lon = 7.809007
car_id = 'Aubf760'


#----------------------------------------------------------------------------------------
#This code is for taking lat and lon from serial monitor 
def set_interval(func, sec):
    def func_wrapper():
        set_interval(func, sec) 
        func()  
    t = threading.Timer(sec, func_wrapper)
    t.start()
    return t

def func():
	global lat, lon
	read_serial = ser.readline()
	lat = float(read_serial[0:9])
	lon = float(read_serial[10:19])
	print(lat,lon)
	socketio.emit('location',{
	 	'lat': lat,
	 	'lon': lon
	 	}, namespace = "/api/location")
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
# SocketIo routes for api
@socketio.on('connect', namespace="/api/location")
def current_location():
	set_interval(func, 1)


#+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


if __name__ == '__main__':
	app.debug = True
	socketio.run(app,host = '0.0.0.0', port=5000)


