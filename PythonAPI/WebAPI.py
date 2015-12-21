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

@app.route("/api/post/message/<message>", methods=['GET','POST'])
def send_message(message):
    return jsonify({'id': db.send_message(message)})

@app.route("/api/post/group/message/<message>", methods=['GET','POST'])
def send_message_group(message):
    return jsonify({'id': db.send_message_group(message)})

@app.route("/api/post/user/<user>", methods=['GET','POST'])
def insert_user(user):
    return jsonify({'id': db.insert_user(user)})

@app.route("/api/get/user/exists/<phone>", methods=['GET','POST'])
def user_exists(phone):
    return jsonify({'response': db.user_exists(phone)})

@app.route("/api/get/friends/<friendList>", methods=['GET','POST'])
def get_friends(friendList):
    return jsonify({'friends': db.get_friends(friendList)})

@app.route("/api/post/user/update/<json>", methods=['GET','POST'])
def update_image(json):
    return jsonify({'result': db.update_image(json)})

if __name__ == "__main__":
    app.run(host='146.185.155.88', port=8080)
