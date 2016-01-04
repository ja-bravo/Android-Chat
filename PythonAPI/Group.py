__author__ = 'Jose Antonio'

class Group():
    def __init__(self):
        self.ID = 0
        self.NAME = ""
        self.ADMIN = 0
        self.IMAGE = ""



    def serialize(self):
        return {
            'ID': self.ID,
            'NAME': self.NAME,
            'ADMIN': self.ADMIN,
            'IMAGE': self.IMAGE
        }