#! /usr/bin/python3

__author__ = 'JoseAntonio'

import pymysql
import json
from User import  User
from Message import  Message


class DataBase():

    def get_users(self):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()
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
        connection.close()
        return users

    def get_user(self, ID):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()
        SQL = "SELECT * FROM USERS WHERE ID_USER = %s OR PHONE = %s;"
        SQL = SQL % (str(ID), str(ID))
        cursor.execute(SQL)

        user = User()
        user.ID = cursor._rows[0][0]
        user.NICK = cursor._rows[0][1]
        user.STATUS = cursor._rows[0][2]
        user.PHONE = cursor._rows[0][3]
        user.USER_IMAGE = cursor._rows[0][4]
        user.LAST_RECEIVED_MESSAGE = cursor._rows[0][5]

        cursor.close()
        connection.close()
        return user.serialize()

    def get_messages(self, ID):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()

        #Conseguir último mensaje individual.
        SQL = """SELECT MAX(SEND_INDIVIDUAL_MESSAGE.ID_MESSAGE) AS MAX_INDIVIDUAL
                 FROM SEND_INDIVIDUAL_MESSAGE""".replace('\n',' ')

        cursor.execute(SQL)
        maxI = cursor._rows[0][0]

        #Conseguir último mensaje grupal.
        SQL = """SELECT MAX(SEND_MESSAGE_GROUP.ID_MESSAGE) AS MAX_GROUP
                FROM SEND_MESSAGE_GROUP""".replace('\n',' ')

        cursor.execute(SQL)
        maxG = cursor._rows[0][0]

        if maxG is None:
            maxG = 0

        if maxI is None:
            maxI = 0

        #Recibir mensajes
        SQL = """SELECT ID_USER_SENDER , MESSAGES.TEXT , 0 AS ID_GROUP
                 FROM USERS , MESSAGES , SEND_INDIVIDUAL_MESSAGE
                 WHERE USERS.ID_USER = SEND_INDIVIDUAL_MESSAGE.ID_USER_RECEIVER
                       AND SEND_INDIVIDUAL_MESSAGE.ID_MESSAGE = MESSAGES.ID_MESSAGE
                       AND USERS.ID_USER = %s
                       AND MESSAGES.ID_MESSAGE > USERS.LAST_RECEIVED_MESSAGE_INDIVIDUAL
                       AND MESSAGES.ID_MESSAGE <= %s
                UNION
                SELECT USERS.ID_USER AS ID_USER_SENDER, MESSAGES.TEXT , BELONG.ID_GROUP
                FROM USERS , MESSAGES , SEND_MESSAGE_GROUP , BELONG
                WHERE USERS.ID_USER = BELONG.ID_USER AND BELONG.ID_GROUP = SEND_MESSAGE_GROUP.ID_GROUP
                      AND SEND_MESSAGE_GROUP.ID_MESSAGE = MESSAGES.ID_MESSAGE AND
                      USERS.ID_USER = %s AND
                      MESSAGES.ID_MESSAGE > USERS.LAST_RECEIVED_MESSAGE_GROUP
                      AND MESSAGES.ID_MESSAGE <= %s ;""".replace('\n',' ')

        SQL = SQL % (str(ID),str(maxI),str(ID),str(maxG))
        cursor.execute(SQL)

        messages = list()
        for row in cursor:
            message = Message()
            message.ID_USER_SENDER = row[0]
            message.TEXT = row[1]
            message.ID_GROUP = row[2]

            messages.append(message.serialize())

        #Actualizar ultimo mensaje.
        SQL = """UPDATE USERS
	             SET USERS.LAST_RECEIVED_MESSAGE_INDIVIDUAL = %s ,
		             USERS.LAST_RECEIVED_MESSAGE_GROUP = %s
	             WHERE USERS.ID_USER = %s ;""".replace('\n',' ').replace('\t','')

        SQL = SQL % (str(maxI),str(maxG),str(ID))
        cursor.execute(SQL)
        connection.commit()

        cursor.close()
        return messages

    def send_message(self, message):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()

        message = json.loads(message)
        text = message["text"]
        ID = message["to"]
        idDest = message["from"]

        SQL = """INSERT INTO MESSAGES ( TEXT , DATE_MESSAGE )
	              VALUES ('%s', sysdate() );
	              """.replace('\n',' ').replace('\t','')
        SQL = SQL % str(text)

        cursor.execute(SQL)
        connection.commit()
        messageID = cursor.lastrowid

        SQL = """INSERT INTO  SEND_INDIVIDUAL_MESSAGE
                 (ID_MESSAGE , ID_USER_RECEIVER, ID_USER_SENDER)
                 VALUES (%s , %s , %s);
	              """.replace('\n',' ')
        SQL = SQL % (str(messageID), str(ID), str(idDest))

        cursor.execute(SQL)
        connection.commit()

        cursor.close()
        connection.close()
        return messageID

    def send_message_group(self, message):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()

        message = json.loads(message)
        text = message["text"]
        ID = message["to"]
        idDest = message["from"]

        SQL = """INSERT INTO MESSAGES ( TEXT , DATE_MESSAGE )
	              VALUES ('%s', sysdate() );
	              """.replace('\n',' ').replace('\t','')
        SQL = SQL % str(text)

        cursor.execute(SQL)
        connection.commit()
        messageID = cursor.lastrowid

        SQL = """INSERT INTO  SEND_MESSAGE_GROUP
                 (ID_MESSAGE , ID_USER , ID_GROUP)
                 VALUES (%s , %s , %s);
	              """.replace('\n',' ')
        SQL = SQL % (str(messageID), str(ID), str(idDest))

        cursor.execute(SQL)
        connection.commit()

        cursor.close()
        connection.close()
        return messageID

    def insert_user(self, user):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()

        user = json.loads(user)
        nick = user["nick"]
        number = user["phone"]

        SQL = """INSERT INTO  USERS  (NICK , PHONE)
                 VALUES ('%s' , '%s');""".replace('\n',' ').replace('\t','')
        SQL = SQL % (str(nick), str(number))

        cursor.execute(SQL)
        connection.commit()

        SQL = """SELECT ID_USER
                 FROM USERS
                 WHERE PHONE = %s""".replace('\n',' ').replace('\t','')
        SQL = SQL % (str(number))
        cursor.execute(SQL)

        ID = cursor._rows[0][0]
        cursor.close()
        connection.close()
        return ID

    def user_exists(self, phone):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()

        SQL = """SELECT COUNT(*)
                 FROM USERS
                 WHERE PHONE = '%s'""".replace('\n',' ').replace('\t','')
        SQL = SQL % (str(phone))

        cursor.execute(SQL)

        result = cursor._rows[0][0]
        cursor.close()
        connection.close()
        return result > 0

    def get_friends(self,friends):
        connection = pymysql.connect(host='146.185.155.88', port=3306, user='androiduser', passwd='12345', db='androidchat')
        cursor = connection.cursor()

        friends = json.loads(friends)

        my_friends = list()

        for friend in friends["Friends"]:
            friend["PHONE"] = friend["PHONE"].replace(' ','').replace('+34','')
            phone = friend["PHONE"].replace(' ','')
            if phone == '':
                phone = 0

            SQL = """SELECT COUNT(*)
                     FROM USERS
                     WHERE PHONE = %s""".replace('\n',' ').replace('\t','')
            SQL = SQL % (str(phone))
            SQL = SQL.replace('\u202c','').replace('\u202a','')
            cursor.execute(SQL)

            count = cursor._rows[0][0]
            if count > 0:
                SQL = """SELECT *
                     FROM USERS
                     WHERE PHONE = %s""".replace('\n',' ').replace('\t','')
                SQL = SQL % (str(phone))
                SQL = SQL.replace('\u202c','').replace('\u202a','')
                cursor.execute(SQL)

                user = User()
                user.ID = cursor._rows[0][0]
                user.NICK = cursor._rows[0][1]
                user.STATUS = cursor._rows[0][2]
                user.PHONE = cursor._rows[0][3]
                user.USER_IMAGE = cursor._rows[0][4]

                my_friends.append({'friend': user.serialize()})

        cursor.close()
        connection.close()
        return my_friends