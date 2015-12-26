__author__ = 'Jose'

class Puntuacion():
    def __init__(self):
        self.LATITUD = 0
        self.LONGITUD = ""
        self.TIEMPO = ""
        self.CLICKS = 0

    def serialize(self):
        return {
            'LATITUD': self.LATITUD,
            'LONGITUD': self.LONGITUD,
            'TIEMPO': self.TIEMPO,
            'CLICKS': self.CLICKS
        }