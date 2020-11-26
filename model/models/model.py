import abc

class Model:
    """Abstract model class
    It forces other models to follow below interfaces
    """
    __metaclass__ = abc.ABCMeta

    def __init__(self):
        pass

    @abc.abstractmethod
    def classify(self, urls):
        pass