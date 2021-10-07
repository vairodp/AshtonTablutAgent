from . import settings as global_settings
from . import pawn as pawn
from . import turn as turn
from . import result as game_result


class __Const:
    def __init__(self, module):
        for setting in dir(module):
            if setting.isupper():
                setattr(self, setting, getattr(module, setting))

    def __setattr__(self, attr, value):
        if not getattr(self, attr, None):
            super().__setattr__(attr, value)
        else:
            raise TypeError("'constant' does not support item assignment")


class __Settings(__Const):
    def __init__(self):
        super().__init__(global_settings)


class __Pawn(__Const):
    def __init__(self):
        super().__init__(pawn)


class __Turn(__Const):
    def __init__(self):
        super().__init__(turn)


class __Result(__Const):
    def __init__(self):
        super().__init__(game_result)


settings = __Settings()
Pawn = __Pawn()
Turn = __Turn()
GameResult = __Result()
