package alien.twitchIntegration;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyListener implements Listener {

    Main plugin;

    public MyListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatMessageEvent(AsyncChatEvent e){
        if(plugin.isConnected && plugin.connectChatTwitch && plugin.chat != null && plugin.connectChatMinecraft){

        }
    }
}
