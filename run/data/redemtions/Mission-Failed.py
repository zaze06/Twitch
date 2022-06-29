from shared import *

redemptionId = [
    "bd87790e-6330-44cd-8f17-461dc8806920"
]
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 30:
        handler.setTime(30)
    elif odds <= 60:
        handler.setTime(60)
