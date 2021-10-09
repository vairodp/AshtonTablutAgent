from abc import ABC, abstractmethod


class Action(ABC):

    from_ = None
    to = None

    @abstractmethod
    def __hash__(self):
        pass

    def __str__(self):
        return f'{self.from_}, {self.to}'
