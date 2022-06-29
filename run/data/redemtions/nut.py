import random
from shared import *

redemptionId = [
    "31702460-3cf4-4576-a299-fe712a1123d2"
]
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 20:
        world = handler.getWorld()
        player = handler.getPlayer().getPlayer()

        pilliger = int((random.random() * 2)) + 2
        vindicators = int((random.random() * 2)) + 5
        witch = int((random.random() * 2)) + 1
        evoker = 2
        ravager_vindicator = int((random.random() * 1)) + 1
        ravager_evoker = 1

        print(str(pilliger)+" "+str(vindicators)+" "+str(witch)+" "+str(evoker)+" "+str(ravager_vindicator)+" "+str(ravager_evoker))


        total = pilliger + vindicators + witch + evoker + ravager_evoker + ravager_vindicator

        i = 0

        while i < total:
            pos = player_pos.clone().set(0, 0, 0)

            pos.setX(int(random.random() * ((player_pos.getX() + 30) - (player_pos.getX() - 30)) + (player_pos.getX() + 30)))
            pos.setZ(int(random.random() * ((player_pos.getZ() + 30) - (player_pos.getZ() - 30)) + (player_pos.getZ() + 30)))
            pos.setY(world.getMaxHeight())

            while world.isAirAt(pos) and pos.getY() > world.getMinHeight():
                pos.addY(-1)

            if pos.getY() == world.getMinHeight():
                pos.setY(player_pos.getY())

                for x in nums(pos.getX(), pos.getX()):
                    for z in nums(pos.getZ()-1, pos.getZ()+1):
                        tmp = pos.clone().set(x, pos.getY(), z)
                        if world.isAirAt(tmp):
                            world.setBlock(tmp, "DIRT")

            if pilliger > 0:
                e = world.spawnEntity(pos, "Pillager")
                e.setSilent(True)
                e.setTarget(player)
                e.setName(user_name)
                pilliger -= 1
            elif vindicators > 0:
                e = world.spawnEntity(pos, "Vindicator")
                e.setSilent(True)
                e.setTarget(player)
                e.setName(user_name)
                vindicators -= 1
            elif witch > 0:
                e = world.spawnEntity(pos, "witch")
                e.setSilent(True)
                e.setTarget(player)
                e.setName(user_name)
                witch -= 1
            elif evoker > 0:
                e = world.spawnEntity(pos, "evoker")
                e.setSilent(True)
                e.setTarget(player)
                e.setName(user_name)
                evoker -= 1
            elif ravager_evoker > 0:
                rider = world.spawnEntity(pos, "Evoker")
                rider.setSilent(True)
                rider.setTarget(player)
                rider.setName(user_name)

                e = world.spawnEntity(pos, "Ravager")
                e.setSilent(True)
                e.setTarget(player)
                e.addPassenger(rider)
                e.setName(user_name)
                ravager_evoker -= 1

            elif ravager_vindicator > 0:
                rider = world.spawnEntity(pos, "Vindicator")
                rider.setSilent(True)
                rider.setTarget(player)
                rider.setName(user_name)

                e = world.spawnEntity(pos, "Ravager")
                e.setSilent(True)
                e.setTarget(player)
                e.addPassenger(rider)
                e.setName(user_name)
                ravager_vindicator -= 1


# run(0, None, None, None, None, 4, None)
