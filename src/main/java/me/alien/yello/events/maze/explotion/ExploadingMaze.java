package me.alien.yello.events.maze.explotion;

import me.alien.yello.events.Event;
import me.alien.yello.events.PrintHandler;
import me.alien.yello.events.RandomEvent;
import me.alien.yello.events.maze.MazeGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static me.alien.yello.Main.plugin;

public class ExploadingMaze implements Event {

    private final PrintHandler out;

    public static void main(String[] args) {
        MazeGenerator maze = new MazeGenerator(11,11);

        int[][] map = maze.getIntMaze();

        int x = (int) (Math.random() * map.length);
        int y = (int) (Math.random() * map[x].length);
        while(map[x][y] != 0){
            x = (int) (Math.random() * map.length);
            y = (int) (Math.random() * map[x].length);
        }
        map[x][y] = 2;

        StringBuilder builder = new StringBuilder();
        for(x = 0; x < map.length; x++){
            for(y = 0; y < map[x].length; y++){
                builder.append(map[x][y]);
            }
            builder.append("\n");
        }
        System.out.println(builder);
    }

    public ExploadingMaze(PrintHandler out){
        out.print("You have ben placed in the center of a maze find the exit");
        this.out = out;
    }

    @Override
    public boolean run() {
        MazeGenerator maze = new MazeGenerator(11,11);

        int[][] map = maze.getIntMaze();

        int x = (int) (Math.random() * map.length-1);
        int z = (int) (Math.random() * map[x].length-1);
        while(map[x][z] != 0){
            x = (int) (Math.random() * map.length-1);
            z = (int) (Math.random() * map[x].length-1);
        }
        //map[x][z] = 2;
        switch ((int) (Math.random() * 3) + 1) {
            case 1 -> map[0][(int) (Math.random() * map[0].length - 1)] = 0;
            case 2 -> map[(int) (Math.random() * map.length - 1)][0] = 0;
            case 3 -> map[map.length - 1][(int) (Math.random() * map[0].length - 1)] = 0;
            case 4 -> map[(int) (Math.random() * map.length - 1)][map[0].length - 1] = 0;
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
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()-2, (int) (pos.getZ()-finalZ)+z1, (map[x1][z1]==0?world.getType((int) (pos.getX()+finalX)+x1, (int) pos.getY(), (int) (pos.getZ()+finalZ)+z1):Material.TNT));
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()-1, (int) (pos.getZ()-finalZ)+z1, Material.STONE);
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY(), (int) (pos.getZ()-finalZ)+z1, Material.STONE_PRESSURE_PLATE);
                    world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()+2, (int) (pos.getZ()-finalZ)+z1, Material.GLASS);
                    if (x1 == 0 || x1 == map.length-1 || z1 == 0 || z1 == map[x1].length-1) {
                        world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY(), (int) (pos.getZ()-finalZ)+z1, (map[x1][z1]==0?Material.AIR:Material.GLASS));
                        world.setType((int) (pos.getX()-finalX)+x1, (int) pos.getY()+1, (int) (pos.getZ()-finalZ)+z1, (map[x1][z1]==0?Material.AIR:Material.GLASS));
                    }
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
