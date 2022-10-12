/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello.events.maze;

import me.alien.yello.Main;
import me.alien.yello.events.Event;
import me.alien.yello.events.PrintHandler;
import me.alien.yello.events.RandomEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static me.alien.yello.Main.plugin;

public class Maze implements Event {

    public Maze(PrintHandler out){
        out.print("You have ben placed in the center of a maze find the exit");
    }

    @Override
    public boolean run() {
        MazeGenerator maze = new MazeGenerator(11,11);

        int[][] map = maze.getIntMaze();

        int x = (int) (Main.rand.nextDouble() * map.length-1);
        int z = (int) (Main.rand.nextDouble() * map[x].length-1);
        while(map[x][z] != 0){
            x = (int) (Main.rand.nextDouble() * map.length-1);
            z = (int) (Main.rand.nextDouble() * map[x].length-1);
        }
        //map[x][z] = 2;
        switch ((int) (Main.rand.nextDouble() * 3) + 1) {
            case 1 -> map[0][(int) (Main.rand.nextDouble() * map[0].length - 1)] = 0;
            case 2 -> map[(int) (Main.rand.nextDouble() * map.length - 1)][0] = 0;
            case 3 -> map[map.length - 1][(int) (Main.rand.nextDouble() * map[0].length - 1)] = 0;
            case 4 -> map[(int) (Main.rand.nextDouble() * map.length - 1)][map[0].length - 1] = 0;
        }

        double finalX = x;
        double finalZ = z;
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            Player player = RandomEvent.player;
            World world = player.getWorld();
            Location pos = player.getLocation();
            player.teleport(new Location(world, ((int)pos.getX())+0.5, ((int)pos.getY()), ((int)pos.getZ())+0.5));
            for (int x1 = 0; x1 < map.length; x1++) {
                for (int z1 = 0; z1 < map[x1].length; z1++) {
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()-1, (int) (pos.getZ()-finalZ)+z1, Material.STONE);
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()+2, (int) (pos.getZ()-finalZ)+z1, Material.STONE);
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY(), (int) (pos.getZ()-finalZ)+z1, (map[x1][z1]==0?Material.AIR:Material.STONE));
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()+1, (int) (pos.getZ()-finalZ)+z1, (map[x1][z1]==0?Material.AIR:Material.STONE));
                }
            }
        });
        return false;
    }

    @Override
    public void addData(Object data) {

    }

    @Override
    public void removeData(Object data) {

    }

    @Override
    public void removeData(int location) {

    }

    @Override
    public Object getData(int location) {
        return null;
    }

    @Override
    public void end() {

    }
}
