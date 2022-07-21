package me.alien.yello.events;

import me.alien.yello.events.maze.Maze;
import me.alien.yello.events.maze.explotion.ExploadingMaze;
import me.alien.yello.events.mindsweper.Minesweeper;
import me.alien.yello.Main;
import me.alien.yello.events.tic.tac.toe.TicTacToe;
import me.alien.yello.util.Time;
import org.bukkit.entity.Player;

import java.util.List;

public class RandomEvent extends Thread{
    public int forceEvent = -1;
    Main plugin;
    public static Event event;
    public RandomEvent(Main plugin) {
        this.plugin = plugin;
    }
    public static Player player;
    public static boolean isRunning = false;
    public Class<? extends Event>[] events = new Class[]{TicTacToe.class, Minesweeper.class, ExploadingMaze.class, Maze.class};


    public static void addData(Object data){
        event.addData(data);
    }

    @Override
    public void run() {
        while(true){
            try{
                long i = new Time(0,0,(int) (Math.random() * (10-1) + 1)).toMilliSec();
                synchronized (this) {
                    wait(i);
                }
                List<? extends Player> players = plugin.getServer().getOnlinePlayers().stream().toList();
                player = players.get((int) (Math.random()*players.size()));

                int selectedEvent = forceEvent==-1?(int) (Math.random() * events.length):forceEvent;
                event = events[selectedEvent].getDeclaredConstructor(PrintHandler.class).newInstance((PrintHandler) data -> player.sendMessage(data));
                forceEvent = -1;
                isRunning = true;
                if(event.run()){
                    Time time = new Time(0, 0, (int) (Math.random() * (5 - 2) + 2));
                    long grace = time.toSec();
                    player.sendMessage("You have won "+time.toMin()+" minutes of grace");
                    plugin.graceTime = (int) grace;
                }
                isRunning = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
