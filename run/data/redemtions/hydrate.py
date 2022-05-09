redemptionId = "3c8cb475-ece9-4003-89f0-2e881eec95f0"
env = "PLUGIN"


def run(cost, userName, user, player, pos):
    ## player.sendMessage("Hi!, " + userName + " redeemed something for " + str(cost))
    vec = pos.clone()
    vec.add(0, 1, 0)
    player.getWorld.setBlock(vec, "COBBLESTONE")
