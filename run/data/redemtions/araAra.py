from shared import *

redemptionId = "74671ad8-542d-497f-be42-f324df092e2b"
env = "PLUGIN"

def run(cost, user_name, user, handler, player_pos, odds, redemption):
    if odds <= 100:#50:
        pos = player_pos
        world = handler.getWorld()
        player = handler.getPlayer()

        e = world.spawnEntity(pos, "Evoker")
        e.setSilent(True)
        e.setTarget(player)
        e.setName(user_name)
        e = world.spawnEntity(pos, "Evoker")
        e.setSilent(True)
        e.setTarget(player)
        e.setName(user_name)

        e = world.spawnEntity(pos, "Vindicator")
        e.setSilent(True)
        e.setTarget(player)
        e.setName(user_name)
        e = world.spawnEntity(pos, "Vindicator")
        e.setSilent(True)
        e.setTarget(player)
        e.setName(user_name)

        player.addEffect("SLOW", 60*20, 2)