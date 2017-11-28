import os
from flask import Flask, render_template ,request ,send_from_directory
import threading
import serial
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

root = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app", "static", "dist")
ser = serial.Serial('/dev/ttyACM0', 9600)
#This code is for taking lat and lon from serial monitor 
#
def set_interval(func, sec):
    def func_wrapper():
        set_interval(func, sec) 
        func()  
    t = threading.Timer(sec, func_wrapper)
    t.start()
    return t

def func():
	read_serial = ser.readLine()
	print(read_serial)

@app.route('/<path:path>', methods=['GET'])
def static_proxy(path):
    return send_from_directory(root, path)

@app.route('/', methods=['GET'])
def redirect_to_index():
	set_interval(func, 1)
	return send_from_directory(root, 'index.html')

@app.route('/login', methods=['GET'])
def redirect_to_login():
	set_interval(func, 1)
	return send_from_directory(root, 'index.html')

@app.route('/dashboard', methods=['GET'])
def redirect_to_dashboard():
    return send_from_directory(root, 'index.html')

if __name__ == '__main__':
	app.debug = True
	app.run(host = '0.0.0.0', port=5000)