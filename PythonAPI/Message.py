__author__ = 'Jose Antonio'


class Message:
    def __init__(self):
        self.ID_USER_SENDER = 0
        self.TEXT = ""
        self.ID_GROUP = 0
        self.PHONE = ""

    def serialize(self):
        return {
            'ID_USER_SENDER': self.ID_USER_SENDER,
            'TEXT': self.TEXT,
            'ID_GROUP': self.ID_GROUP,
            'PHONE': self.PHONE
        }
