from shared import *

redemptionId = [
    "b7615235-3510-4ba9-a9e7-20ad3725f073",
    "8f1929f0-1d13-4a2d-834b-67e3d936cc2e"
]
env = "PLUGIN"


def run(cost, user_name, user, handler, pos1, odds, redemption):
    player = handler.getPlayer()
    world = player.getWorld()
    pos = player.getPos()

    for x in nums(pos.getX()-50, pos.getX()+50):
        for y in nums(pos.getY()-50, pos.getY()+50):
            world.setBlock(pos.clone().set(x, y, pos.getZ()+50), "WATER", "AIR")

    for x in nums(pos.getX()-50, pos.getX()+50):
        for y in nums(pos.getY()-50, pos.getY()+50):
            world.setBlock(pos.clone().set(x, y, pos.getZ()-50), "WATER", "AIR")

    for z in nums(pos.getZ()-50, pos.getZ()+50):
        for y in nums(pos.getY()-50, pos.getY()+50):
            world.setBlock(pos.clone().set(pos.getX()+50, y, z), "WATER", "AIR")

    for z in nums(pos.getX()-50, pos.getX()+50):
        for y in nums(pos.getY()-50, pos.getY()+50):
            world.setBlock(pos.clone().set(pos.getX()-50, y, z), "WATER", "AIR")
