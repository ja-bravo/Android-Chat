#! /usr/bin/python3

__author__ = 'JoseAntonio'

from DataBase import DataBase
from flask import Flask, jsonify

db = DataBase()
app = Flask(__name__)

@app.route("/api/get/users/", methods=['GET','POST'])
def get_users():
    return jsonify({'users': db.get_users()})

@app.route("/api/get/users/<int:id>", methods=['GET','POST'])
def get_user(id):
    return jsonify({'user': db.get_user(id)})

@app.route("/api/get/messages/<int:id>", methods=['GET','POST'])
def get_messages(id):
    return jsonify({'messages': db.get_messages(id)})

@app.route("/api/post/message/<int:id>&<message>&<int:idDest>", methods=['GET','POST'])
def send_message(id,message,idDest):
    return jsonify({'id': db.send_message(id,message,idDest)})

@app.route("/api/post/user/<nick>&<int:number>", methods=['GET','POST'])
def insert_user(nick,number):
    return jsonify({'id': db.insert_user(nick,number)})

@app.route("/api/get/user/exists/<phone>", methods=['GET','POST'])
def user_exists(phone):
    return jsonify({'response': db.user_exists(phone)})

@app.route("/api/get/friends/<friendList>", methods=['GET','POST'])
def get_friends(friendList):
    return jsonify({'friends': db.get_friends(friendList)})

if __name__ == "__main__":
    app.run(host='146.185.155.88', port=8080)
