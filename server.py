import os
from flask import Flask, render_template ,request ,send_from_directory

app=Flask(__name__)

root = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app", "static", "dist", "efficycle")

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
