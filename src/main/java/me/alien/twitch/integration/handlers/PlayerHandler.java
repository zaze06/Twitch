package me.alien.twitch.integration.handlers;

import me.alien.twitch.integration.Main;
import me.alien.twitch.integration.util.Vector3I;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerHandler {
    private final Player p;
    private final Main plugin;

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

    public Vector3I getPos(){
        Location pos = p.getLocation();
        return new Vector3I(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
    }

    public boolean addEffect(String effect, int duration, int amplification){
        AtomicBoolean success = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            PotionEffectType eff = PotionEffectType.getByName(effect);
            if(eff == null){
                return;
            }
            p.addPotionEffect(new PotionEffect(eff, duration, amplification));
            success.set(true);
        });
        return success.get();
    }

    public WorldHandler getWorld(){
        return new WorldHandler(p.getWorld(), plugin);
    }

    public Player getPlayer() {
        return p;
    }
}
