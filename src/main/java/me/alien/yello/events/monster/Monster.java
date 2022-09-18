package me.alien.yello.events.monster;

import me.alien.yello.events.Event;
import me.alien.yello.events.PrintHandler;
import org.bukkit.entity.Warden;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.concurrent.atomic.AtomicReference;

import static me.alien.yello.Main.plugin;
import static me.alien.yello.events.RandomEvent.player;

public class Monster implements Event {
    PrintHandler out;
    final Object obj = new Object();

    @Override
    public boolean run() {
        out.print("Ready? recommendation, Run! you have 5 seconds :)");
        synchronized (obj){
            try {
                obj.wait(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        out.print("Hear it comes!");
        AtomicReference<Warden> warden = new AtomicReference<>();
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            warden.set(player.getWorld().spawn(player.getLocation(), Warden.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (warden1) -> {
                warden1.setTarget(player);
                warden1.setSilent(true);
            }));
        });
        boolean win = false;
        while(warden.get() == null){
            synchronized (obj){
                try {
                    obj.wait(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        while((!player.isDead()) && !(warden.get().isDead())){
            synchronized (obj){
                try {
                    obj.wait(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(warden.get().isDead()){
            win = true;
        }
        return win;
    }

    public Monster(PrintHandler out){
        this.out = out;
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
