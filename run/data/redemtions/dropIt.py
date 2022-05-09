from shared import *

redemptionId = "cfc356cb-756c-49d8-b6c1-6c71c126e973"
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 100:#20:
        pos = handler.getPlayer().getPos()

        for y in nums(pos.getY()+4, -60):
            for x in nums(pos.getX()-2, pos.getX()+2):
                for z in nums(pos.getZ()-2, pos.getZ()+2):
                    handler.getWorld().setBlock(pos.copy().set(x, y, z), "AIR")
