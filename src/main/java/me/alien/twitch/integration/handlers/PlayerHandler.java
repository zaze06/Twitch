package me.alien.twitch.integration.handlers;

import me.alien.twitch.integration.Main;
import org.bukkit.entity.Player;

public class PlayerHandler {
    final Player p;
    final Main plugin;

    public PlayerHandler(Player p, Main plugin) {
        this.p = p;
        this.plugin = plugin;
    }

    public void sendMessage(String message){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            p.sendMessage(message);
        });
    }

    public void setHp(double hp){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            p.setHealth(hp);
        });
    }

    public void addHp(double hp){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            p.setHealth(p.getHealth() + hp);
        });
    }

    public void removeHp(double hp){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            p.setHealth(p.getHealth() - hp);
        });
    }

    public WorldHandler getWorld(){
        return new WorldHandler(p.getWorld(), plugin);
    }
}
