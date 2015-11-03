__author__ = 'JoseAntonio'

class User():
    def __init__(self):
        self.ID = 0
        self.PHONE = 0
        self.STATUS = ""
        self.NAME = ""

    def serialize(self):
        return {
            'ID': self.ID,
            'PHONE': self.PHONE,
            'STATUS': self.STATUS,
            'NAME': self.NAME
        }