package alien.twitchIntegration;

import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MyListener implements Listener {

    Main plugin;

    public MyListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatMessageEvent(AsyncChatEvent e){
        if(plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.CHAT)){
            plugin.twitchClient.getChat().sendMessage(plugin.chat, "<"+e.getPlayer().displayName()+"> "+PaperComponents.plainTextSerializer().serialize(e.message()));
        }
    }

    @EventHandler
    public void onDeathEvent(PlayerDeathEvent e) {
        if (plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.INFO)) {
            plugin.twitchClient.getChat().sendMessage(plugin.chat, PaperComponents.plainTextSerializer().serialize(e.deathMessage()));
        }
    }
}
