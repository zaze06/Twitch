package me.alien.twitch.integration.handlers;

import me.alien.twitch.integration.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class EntityHandler {
    private Entity e;
    private Main plugin;

    public EntityHandler(Entity e, Main plugin) {
        this.e = e;
        this.plugin = plugin;
    }

    public void setSilent(boolean silent){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            e.setSilent(silent);
        });
    }

    public boolean setTarget(EntityHandler e){
        return setTarget(e.getEntity());
    }

    public boolean setTarget(PlayerHandler e){
        return setTarget(e.getPlayer());
    }

    public boolean setTarget(Entity e){
        AtomicBoolean success = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(this.e instanceof Monster me && e instanceof LivingEntity le){
                me.setTarget(le);
                success.set(true);
            }
        });
        return success.get();
    }

    public Entity getEntity(){
        return e;
    }

    public void setName(String name){
        e.customName(Component.text(name));
    }

    public boolean addPassenger(Entity e){
        AtomicBoolean success = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            success.set(this.e.addPassenger(e));
        });
        return success.get();
    }

    public boolean setPowered(boolean powered){
        AtomicBoolean success = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(e instanceof Creeper c){
                c.setPowered(powered);
            }
        });
        return success.get();
    }

    public String getType(){
        AtomicReference<String> type = new AtomicReference<>("");
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            type.set(e.getType().name());
        });
        return type.get();
    }

}
