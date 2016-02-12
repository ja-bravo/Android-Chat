__author__ = 'Jose Antonio'


class User:
    def __init__(self):
        self.ID = 0
        self.NICK = ""
        self.STATUS = ""
        self.PHONE = 0
        self.USER_IMAGE = ""
        self.LAST_RECEIVED_MESSAGE = 0

    def serialize(self):
        return {
            'ID': self.ID,
            'NICK': self.NICK,
            'STATUS': self.STATUS,
            'PHONE': self.PHONE,
            'USER_IMAGE': self.USER_IMAGE,
            'LAST_RECEIVED_MESSAGE': self.LAST_RECEIVED_MESSAGE
        }
