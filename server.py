import os
from flask import Flask, render_template ,request ,send_from_directory
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

@app.route('/<path:path>', methods=['GET'])
def static_proxy(path):
    return send_from_directory(root, path)


@app.route('/', methods=['GET'])
def redirect_to_index():
    return send_from_directory(root, 'index.html')

if __name__ == '__main__':
	app.debug = True
	app.run(host = '', port=5000)