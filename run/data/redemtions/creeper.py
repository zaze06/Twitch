from shared import *

env = "PLUGIN"
redemptionId = "5ca2b532-83b4-4821-b0b7-2205c9c9da28"


def run(cost, user_name, user, handler, pos, odds, redemption):
    world = handler.getWorld()
    player = handler.getPlayer()
    if odds <= 100:#5:
        e = world.spawnEntity(pos, "CREEPER")
        e.setSilent(True)
        e.setPowerd(True)
        e.setTarget(player)
        e.setName(user_name)
    elif odds <= 40:
        e = world.spawnEntity(pos, "CREEPER")
        e.setSilent(True)
        e.setTarget(player)
        e.setName(user_name)
