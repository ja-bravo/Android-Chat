__author__ = 'Jose Antonio'


class Group:
    def __init__(self):
        self.ID = 0
        self.NAME = ""
        self.ADMIN = 0
        self.IMAGE = ""
        self.USERS = ""

    def serialize(self):
        return {
            'ID': self.ID,
            'NAME': self.NAME,
            'ADMIN': self.ADMIN,
            'IMAGE': self.IMAGE,
            'USERS': self.USERS
        }
