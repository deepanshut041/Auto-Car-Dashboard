import os
from flask import Flask, render_template ,request ,send_from_directory
from flask_mail import Mail, Message

app=Flask(__name__)
app.config['MAIL_SERVER']='smtp.gmail.com'
app.config['MAIL_PORT'] = 465
app.config['MAIL_USERNAME'] = 'tyagideepu133@gmail.com'
app.config['MAIL_PASSWORD'] = 'iluvmyself'
app.config['MAIL_USE_TLS'] = False
app.config['MAIL_USE_SSL'] = True
mail=Mail(app)
import RPi.GPIO as GPIO


# ----------------- Status Variables -----------
status = {
    "lockStatus":False,
    "id": 6,
    "lat": 31.2593,
    "lon": 75.7007,
    "speed": 0,
    "emergency": False,
    "batteryPercent": 90
}
print(status)
GPIO.setmode(GPIO.BCM)
GPIO.setup(25, GPIO.OUT, initial=GPIO.LOW)
root = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app", "static", "dist", "efficycle")


@app.route('/shutdown')
def shutdown():
    os.system("sudo shutdown -h now")

@app.route("/emergency")
def index():
#    msg = Message('Emergency', sender = 'tyagideepu133@gmail.com', recipients = ['deepanshut041@gmail.com', 'rhtsnh60@gmail.com'])
#    msg.body = "Hello there is an emergency with Deepanshu Tyagi at: " + str(status['lat'])  + str(status['lon'])
#    mail.send(msg)
   return "Sent"

@app.route('/unlock')
def unlock():
    if(status['lockStatus']):
       GPIO.output(25, GPIO.LOW) 
       status['lockStatus'] = False
    else:
        GPIO.output(25, GPIO.HIGH) # pin 22 first wing
        status['lockStatus'] = True
    return "done"

@app.route('/<path:path>', methods=['GET'])
def static_proxy(path):
    return send_from_directory(root, path)

@app.route('/', methods=['GET'])
def redirect_to_index():
    return send_from_directory(root, 'index.html')


@app.route('/home', methods=['GET'])
def redirect_to_login():
    # set_interval(func, 1)
    return send_from_directory(root, 'index.html')


if __name__ == '__main__':
    app.debug = True
    app.run(host = '0.0.0.0', port=5000)
