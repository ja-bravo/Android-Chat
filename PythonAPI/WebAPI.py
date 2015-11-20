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

@app.route("/api/get/messages/<int:id>", methods=['GET','POST'])
def get_messages(id):
    return jsonify({'messages': db.get_messages(id)})

@app.route("/api/post/message/<int:id>&<message>&<int:idDest>", methods=['GET','POST'])
def send_message(id,message,idDest):
    return jsonify({'id': db.send_message(id,message,idDest)})

@app.route("/api/post/user/<nick>&<number>")
def insert_user(nick,number):
    return jsonify({'ID': db.insert_user(nick,number)})

if __name__ == "__main__":
    app.run(host='146.185.155.88', port=8080)
