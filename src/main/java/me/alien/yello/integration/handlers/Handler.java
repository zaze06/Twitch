package me.alien.yello.integration.handlers;

import me.alien.yello.integration.Main;
import org.bukkit.entity.Player;

public class Handler {
    private final PlayerHandler playerHandler;
    private final Player player;
    private final Main plugin;


    public Handler(Player p, Main plugin){
        playerHandler = new PlayerHandler(p, plugin);
        player = p;
        this.plugin = plugin;
    }

    public PlayerHandler getPlayer(){
        return playerHandler;
    }

    public WorldHandler getWorld(){
        return playerHandler.getWorld();
    }

    public void setTime(int time){
        plugin.time = time;
    }
}
