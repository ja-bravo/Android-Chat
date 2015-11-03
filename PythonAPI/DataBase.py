#! /usr/bin/python3

__author__ = 'JoseAntonio'

import pymysql
from User import  User

class DataBase():

    connection = None

    def __init__(self):
        self.connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')

    def get_users(self):
        cursor = self.connection.cursor()
        cursor.execute("SELECT * FROM USERS")

        users = list()

        for row in cursor:
            user = User()
            user.ID = row[0]
            user.NICK = row[1]
            user.STATUS = row[2]
            user.PHONE = row[3]
            user.USER_IMAGE = row[4]
            user.LAST_RECEIVED_MESSAGE = row[5]

            users.append(user.serialize())

        cursor.close()
        return users

    def get_user(self, ID):
        cursor = self.connection.cursor()
        cursor.execute("SELECT * FROM USERS WHERE ID_USER = " + str(ID))

        user = User()
        user.ID = cursor._rows[0][0]
        user.NICK = cursor._rows[0][1]
        user.STATUS = cursor._rows[0][2]
        user.PHONE = cursor._rows[0][3]
        user.USER_IMAGE = cursor._rows[0][4]
        user.LAST_RECEIVED_MESSAGE = cursor._rows[0][5]

        cursor.close()
        return user.serialize()