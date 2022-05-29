from shared import *

redemptionId = "cfc356cb-756c-49d8-b6c1-6c71c126e973"
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 20:
        pos = handler.getPlayer().getPos()

        # print("x: {max: "+str(pos.getX()-2)+","+str(pos.getX()+2)+"}")
        # print("y: {max: "+str(pos.getY()+4)+","+str(-60)+"}")
        # print(nums(pos.getY()+4, -60))
        # print("z: {max: "+str(pos.getZ()-2)+","+str(pos.getZ()+2)+"}")

        for y in nums(-60, pos.getY()+4):
            for x in nums(pos.getX()-4, pos.getX()+4):
                for z in nums(pos.getZ()-4, pos.getZ()+4):
                    if not handler.getWorld().setBlock(pos.clone().set(x, y, z), "AIR"):
                        print("failed, "+pos.clone().set(x, y, z).toString())

        for x in nums(pos.getX()-4, pos.getX()+4):
            for z in nums(pos.getZ()-4, pos.getZ()+4):
                if not handler.getWorld().setBlock(pos.clone().set(x, -60, z), "LAVA"):
                    print("failed, "+pos.clone().set(x, -60, z).toString())
