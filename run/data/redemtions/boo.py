from shared import *

redemptionId = "00e5da68-b07e-4f6f-9768-2a3b224a27d9"
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 70:
        handler.getPlayer().addEffect("BLINDNESS", 400, 3)
