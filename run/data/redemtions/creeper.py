from shared import *

env = "PLUGIN"
redemptionId = [
    "5ca2b532-83b4-4821-b0b7-2205c9c9da28",
    "924f46c7-51a1-425d-80e1-fb84e858b449"
]


def run(cost, user_name, user, handler, pos, odds, redemption):
    world = handler.getWorld()
    player = handler.getPlayer()
    ##print("test")
    ##if odds <= 40:  # 5:
    ##    e = world.spawnEntity(pos, "CREEPER")
    ##    print(e)
    ##    e.setSilent(True)
    ##    e.setPowerd(True)
    ##    e.setTarget(player)
    ##    e.setName(user_name)
    if odds <= 50:
        e = world.spawnEntity(pos, "CREEPER")
        print(e)
        e.setSilent(True)
        e.setTarget(player)
        e.setName(user_name)
        if odds > 40:
            e.setPowerd(True)
