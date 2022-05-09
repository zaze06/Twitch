from shared import *

env = "PLUGIN"
redemptionId = "5ca2b532-83b4-4821-b0b7-2205c9c9da28"


def run(cost, user_name, user, handler, pos, odds):
    world = handler.getWorld()
    if odds <= 5:
        e = world.spawnEntity(pos, "CREEPER")
        e.setSilent(True)
        e.setPowerd(True)
        handler.setName(e, user_name)
    elif odds <= 40:
        e = world.spawnEntity(pos, "CREEPER")
        e.setSilent(True)
        handler.setName(e, user_name)
