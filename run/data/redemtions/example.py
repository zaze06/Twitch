# A shared library for all redemption handlers(this fill get expanded over time)
# DO NOT modify this file if you need a new shared library for all your redemption handlers make your own
from shared import *
# rest of library's you might want to use under hear
# example: "from random import random" this adds the random function whits you can use to get a
# random number between 0 and 1(example later)
# from random import random

# Required variables:
# redemptionId: this is the id of the redemption this will handel. substitute use redemptionName
# (note this is not reliable since the name can change)
redemptionId = "XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"
# redemptionName: this si the name of the redemption this will handle. it's better to use the redemptionId instead
redemptionName = "XXXX"
# env: this is the environment which is where this redemption handel can be used possible assignments:
# PLUGIN, MOD, BOTH. where they define where it can be used so PLUGIN is only plugin, MOD is only mod
# and BOTH is both PLUGIN and MOD(only use both if you ar only using the handlers and not the exposed
# world or any other part)
env = "PLUGIN"
# other variables you might want


# run function:
# run function is the function that will be called if the redemption that this file will handel is called args:
# cost: the cost of the redemption
# user_name: the name of the twitch user who redeemed it
# user: the user who redeemed it
# handel: a java class handler for minecraft related things check me.alien.twitch.integration.handlers package for all
# the handlers and how to get to them and what they contain.
# note the player and world classes can be exposed using "handler.getPlayer().getPlayer()" to get the player
# player_pos: the position behind the player
# odds: a random number between 0 and 100
# redemption: is the redemption data from the redemption. this contains for example the user input
def run(cost, user_name, user, handler, player_pos, odds, redemption):
    # example handler
    handler.getPlayer().sendMessage(user_name+" redeemed "+redemptionName+" for "+str(cost))
    # this will send a message to the player and inform that the twitch user redeemed the redemption for cost
