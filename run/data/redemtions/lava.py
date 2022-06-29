from shared import *

redemptionId = [
    "0b7209cb-fb66-4b2a-be82-cb614359b54f"
]
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 25:
        world = handler.getWorld()
        pos = handler.getPlayer().getPos()

        for x in nums(pos.getX()-5, pos.getX()+5):
            for y in nums(pos.getY()-5, pos.getY()+5):
                world.setBlock(pos.clone().set(x, y, pos.getZ()+5), "LAVA", "AIR")

        for x in nums(pos.getX()-5, pos.getX()+5):
            for y in nums(pos.getY()-5, pos.getY()+5):
                world.setBlock(pos.clone().set(x, y, pos.getZ()-5), "LAVA", "AIR")

        for z in nums(pos.getZ()-5, pos.getZ()+5):
            for y in nums(pos.getY()-5, pos.getY()+5):
                world.setBlock(pos.clone().set(pos.getX()+5, y, z), "LAVA", "AIR")

        for z in nums(pos.getX()-5, pos.getX()+5):
            for y in nums(pos.getY()-5, pos.getY()+5):
                world.setBlock(pos.clone().set(pos.getX()-5, y, z), "WATER", "AIR")
