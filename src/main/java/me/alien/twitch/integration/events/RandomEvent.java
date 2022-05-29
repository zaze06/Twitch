package me.alien.twitch.integration.events;

import me.alien.twitch.integration.Main;
import me.alien.twitch.integration.events.tic.tac.toe.TicTacToe;
import me.alien.twitch.integration.util.Time;
import org.bukkit.entity.Player;

import java.util.List;

public class RandomEvent extends Thread{
    Main plugin;
    public static Event event;
    public RandomEvent(Main plugin) {
        this.plugin = plugin;
    }
    public static Player player;
    public static boolean isRunning = false;
    public Class<? extends Event>[] events = new Class[]{TicTacToe.class};


    public static void addData(Object data){
        event.addData(data);
    }

    @Override
    public void run() {
        while(true){
            try{
                long i = new Time(0,0,(int) (Math.random() * (20-5) + 5)).toMilliSec();
                synchronized (this) {
                    wait(i);
                }
                List<? extends Player> players = plugin.getServer().getOnlinePlayers().stream().toList();
                player = players.get((int) (Math.random()*players.size()));
                event = events[(int)(Math.random()*events.length)].getDeclaredConstructor(PrintHandler.class).newInstance((PrintHandler) data -> player.sendMessage(data));
                isRunning = true;
                if(event.run()){
                    long grace = new Time(0,0, (int) (Math.random() * (5-2) + 2)).toSec();
                    plugin.graceTime = (int) grace;
                }
                isRunning = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
