package me.alien.twitch.integration.handlers;

import me.alien.twitch.integration.Main;
import me.alien.twitch.integration.util.Vector2I;
import org.bukkit.Material;
import org.bukkit.World;

public class WorldHandler {
    final World world;
    final Main plugin;
    public WorldHandler(World world, Main plugin){
        this.world = world;
        this.plugin = plugin;
    }

    public void setBlock(Vector2I pos, String block){
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            this.world.setType(pos.getX(), pos.getY(), pos.getZ(), Material.valueOf(block));
        });
    }
}
