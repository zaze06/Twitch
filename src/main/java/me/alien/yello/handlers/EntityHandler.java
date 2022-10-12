/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.handlers;

import me.alien.yello.Main;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class EntityHandler {
    private final Entity e;
    private final Main plugin;

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
            if(this.e instanceof Monster me && e instanceof LivingEntity le) {
                me.setTarget(le);
                success.set(true);
                synchronized (success) {
                    success.notifyAll();
                }
            }
        });
        try {
            synchronized (success) {
                success.wait();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return success.get();
    }

    public Entity getEntity(){
        return e;
    }

    public void setName(String name){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            e.customName(Component.text(name));
        });
    }

    public boolean addPassenger(EntityHandler e){
        AtomicBoolean success = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            success.set(this.e.addPassenger(e.getEntity()));
            synchronized (success) {
                success.notifyAll();
            }
        });
        try {
            synchronized (success) {
                success.wait();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return success.get();
    }

    public void setPowered(boolean powered){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(e instanceof Creeper c) {
                c.setPowered(powered);
            }
        });
    }

    public String getType(){
        AtomicReference<String> type = new AtomicReference<>("");
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            type.set(e.getType().name());
            synchronized (type) {
                type.notifyAll();
            }
        });
        try {
            synchronized (type) {
                type.wait();
            }
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        return type.get();
    }

}
