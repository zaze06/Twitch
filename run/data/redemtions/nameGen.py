from shared import *

redemptionId = "983d7367-3a9e-4d3b-a628-ad215850e8f9"
env = "PLUGIN"


def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 100:
        world = handler.getWorld()

        e = world.getRandomEntityInWorld()
        e = world.spawnEntity(player_pos, e.getType())
        e.setName(redemption.getUserInput())

        e = world.getRandomEntityInWorld()
        e = world.spawnEntity(player_pos, e.getType())
        e.setName(redemption.getUserInput())

