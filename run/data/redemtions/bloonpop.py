import random
from shared import *

env = "PLUGIN"
redemptionId = "3c8cb475-ece9-4003-89f0-2e881eec95f0"
potion = ["BLINDNESS", "POISON", "BAD_OMEN", "WITHER"]


def run(cost, user_name, user, handler, pos1, odds, redemption):
    world = handler.getWorld()
    player = handler.getPlayer()
    pos = player.getPos()

    if odds <= 100:#50:
        for x in nums(-3, 3):
            for y in nums(-3, 3):
                for z in nums(-3, 3):
                    world.setBlock(pos.clone().add(x, y, z), "AIR")

        player.addEffect(random.choice(potion), 800, 4)
        player.addEffect(random.choice(potion), 800, 4)
