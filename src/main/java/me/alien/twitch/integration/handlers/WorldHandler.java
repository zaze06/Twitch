package me.alien.twitch.integration.handlers;

import me.alien.twitch.integration.Main;
import me.alien.twitch.integration.util.Vector3I;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class WorldHandler {
    final World world;
    final Main plugin;
    public WorldHandler(World world, Main plugin){
        this.world = world;
        this.plugin = plugin;
    }

    public boolean setBlock(Vector3I pos, String block){
        Material mat;
        try{
            mat = Material.valueOf(block.toUpperCase());
        }catch (Exception e){
            return false;
        }
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            this.world.setType(pos.getX(), pos.getY(), pos.getZ(), mat);
        });
        return true;
    }

    public boolean setBlock(Vector3I pos, String block, String... replace){
        ArrayList<Material> replaceList = new ArrayList<>();

        for(String string : replace){
            Material mat;
            try{
                mat = Material.valueOf(block.toUpperCase());
            }catch (Exception e){
                continue;
            }
            replaceList.add(mat);
        }

        if(replaceList.isEmpty()) return false;

        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if(!replaceList.contains(this.world.getType(pos.getX(), pos.getY(), pos.getZ()))){
                this.world.setType(pos.getX(), pos.getY(), pos.getZ(), Material.valueOf(block.toUpperCase()));
            }
        });

        return true;
    }

    public String getBlock(Vector3I pos){
        AtomicReference<String> type = new AtomicReference<>("");
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            type.set(world.getType(pos.getX(), pos.getY(), pos.getZ()).name());
        });
        return type.get();
    }

    public boolean isAirAt(Vector3I pos){
        AtomicBoolean isAir = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            isAir.set(world.getType(pos.getX(), pos.getY(), pos.getZ()).isAir());
        });
        return isAir.get();
    }

    public EntityHandler spawnEntity(Vector3I pos, String name){
        AtomicReference<Entity> e = new AtomicReference<>();
        AtomicBoolean test = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Location loc = new Location(world, pos.getX(), pos.getY(), pos.getZ());
            e.set(this.world.spawnEntity(loc, EntityType.valueOf(name.toUpperCase())));
            synchronized (test) {
                test.notifyAll();
            }
        });
        try{
            synchronized (test) {
                test.wait();
            }
        }catch (InterruptedException ignored){}
        return new EntityHandler(e.get(), plugin);
    }

    public EntityHandler getRandomEntityInWorld(){
        AtomicReference<Entity> e = new AtomicReference<>();
        AtomicBoolean test = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            e.set(world.getLivingEntities().get((int) (Math.random() * world.getLivingEntities().size())));
            test.set(true);
            synchronized (test) {
                test.notifyAll();
            }
        });
        try{
            synchronized (test) {
                test.wait();
            }
        }catch (InterruptedException ignored){}
        return new EntityHandler(e.get(), plugin);
    }

    public int getMaxHeight(){
        AtomicInteger max = new AtomicInteger();
        AtomicBoolean test = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            max.set(world.getMaxHeight());
            test.set(true);
            synchronized (test) {
                test.notifyAll();
            }
        });
        try{
            synchronized (test) {
                test.wait();
            }
        }catch (InterruptedException ignored){}
        return max.get();
    }

    public int getMinHeight(){
        AtomicInteger min = new AtomicInteger();
        AtomicBoolean test = new AtomicBoolean(false);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            min.set(world.getMinHeight());
            test.set(true);
            synchronized (test) {
                test.notifyAll();
            }
        });
        try{
            synchronized (test) {
                test.wait();
            }
        }catch (InterruptedException ignored){}
        return min.get();
    }


}
