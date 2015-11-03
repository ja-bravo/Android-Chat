#! /usr/bin/python3

__author__ = 'JoseAntonio'

from DataBase import DataBase
from flask import Flask, jsonify

db = DataBase()
app = Flask(__name__)

@app.route("/api/get/users/")
def get_users():
    return jsonify({'users': db.get_users()})

@app.route("/api/get/users/<int:id>")
def get_user(id):
    return jsonify({'user': db.get_user(id)})

if __name__ == "__main__":
    app.run(host='146.185.155.88', port=8080)
