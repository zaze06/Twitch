/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.EnumSet;

public class SkeletonGoal implements Goal<Skeleton> {

    private final GoalKey<Skeleton> key;
    private final Main plugin; // Main class
    private final Mob owner; // The Mob that this goal is assigned too
    private LivingEntity target; // The entity that it shall target

    public SkeletonGoal(Main plugin, Mob owner) {
        this.plugin = plugin;
        this.owner = owner;
        this.key = GoalKey.of(Skeleton.class, new NamespacedKey(plugin, "kill_non_friends"));
    }

    @Override
    public boolean shouldActivate() {
        if(target != null) {
            if (!target.isDead()) return true;
        }
        Collection<LivingEntity> entitys = owner.getLocation().getNearbyLivingEntities(20, e -> e instanceof Monster);
        double distance = 20;
        LivingEntity closest = null;
        for(LivingEntity e : entitys){
            if(e.isDead()) continue;
            if(plugin.friendlyTeam.hasEntity(e)) continue;
            if(e.getLocation().distanceSquared(owner.getLocation()) < distance){
                distance = e.getLocation().distanceSquared(owner.getLocation());
                closest = e;
            }
        }
        if(closest == null){
            return false;
        }
        if(closest.isDead()){
            return false;
        }
        target = closest;
        return true;
    }

    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
        owner.getPathfinder().stopPathfinding();
        owner.setTarget(null);
    }

    @Override
    public void tick() {
        if(owner.getTarget() != null) {
            if (plugin.friendlyTeam.hasEntity(owner.getTarget())) {
                Collection<LivingEntity> entitys = owner.getLocation().getNearbyLivingEntities(20, e -> e instanceof Monster);
                double distance = 20;
                LivingEntity closest = null;
                for(LivingEntity e : entitys){
                    if(e.isDead()) continue;
                    if(plugin.friendlyTeam.hasEntity(e)) continue;
                    if(e.getLocation().distanceSquared(owner.getLocation()) < distance){
                        distance = e.getLocation().distanceSquared(owner.getLocation());
                        closest = e;
                    }
                }
                if(closest != null) {
                    if (!closest.isDead()) {
                        target = closest;
                    }
                }
            }
        }
        if(plugin.host.getLocation().distanceSquared(owner.getLocation()) > 5){
            if(owner.getTarget() != null) owner.setTarget(null);
            owner.getPathfinder().moveTo(plugin.host);
        }else{
            owner.setTarget(target);
        }
    }

    @Override
    public @NotNull GoalKey<Skeleton> getKey() {
        return key;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return EnumSet.of(GoalType.MOVE, GoalType.LOOK, GoalType.TARGET);
    }
}
