#! /usr/bin/python3

__author__ = 'JoseAntonio'

import pymysql
from User import  User

class DataBase():

    conn = None
    cur = None

    def __init__(self):
        self.conn = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')

    def get_users(self):
        cursor = self.conn.cursor()
        cursor.execute("SELECT * FROM USERS")

        users = list()

        for row in cursor:
            user = User()
            user.ID = row[0]
            user.PHONE = row[1]
            user.STATUS = row[2]
            user.NAME = row[3]

            users.append(user.serialize())

        return users

    def get_user(self, ID):
        cursor = self.conn.cursor()
        cursor.execute("SELECT * FROM USERS WHERE ID = " + str(ID))

        user = User()
        user.ID = cursor._rows[0][0]
        user.PHONE = cursor._rows[0][1]
        user.STATUS = cursor._rows[0][2]
        user.NAME = cursor._rows[0][3]

        return user.serialize()