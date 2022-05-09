package me.alien.twitch.integration.handlers;

import me.alien.twitch.integration.Main;
import me.alien.twitch.integration.util.Vector2I;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
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

    public void setName(String name, Entity e){
        e.customName(Component.text(name));
    }

    public void setGraceTime(int time){
        plugin.time = time;
    }
}