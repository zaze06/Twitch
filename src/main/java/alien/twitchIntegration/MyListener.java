package alien.twitchIntegration;

import com.github.twitch4j.TwitchClientHelper;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.tmi.domain.Chatters;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.List;

public class MyListener implements Listener {

    Main plugin;

    public MyListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatMessageEvent(AsyncChatEvent e){
        if(plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.CHAT)){
            plugin.twitchClient.getChat().sendMessage(plugin.chat, "<"+e.getPlayer().getName()+"> "+PaperComponents.plainTextSerializer().serialize(e.message()));
        }
    }

    @EventHandler
    public void onDeathEvent(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player p){
            if(p.getHealth()-e.getDamage() <= 0){
                if (plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.INFO)) {
                    //plugin.twitchClient.getChat().sendMessage(plugin.chat, PaperComponents.plainTextSerializer().serialize(e.deathMessage()));
                }
                Component component = e.getDamager().customName();
                if(component != null) {
                    String cName = PaperComponents.plainTextSerializer().serialize(component);

                    User user = null;
                    try {
                        user = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, Collections.singletonList(cName)).execute().getUsers().get(0);
                        plugin.getServer().getLogger().info("found user " + user.getDisplayName());
                    } catch (Exception ignore) {
                    }
                    if (user != null) {
                        synchronized (plugin.viewerPoints) {
                            Integer points = plugin.viewerPoints.get(user.getId());
                            plugin.viewerPoints.put(user.getId(), points + 100);
                            plugin.getServer().getLogger().info("added " + points + " to " + user.getDisplayName());
                        }
                    } else {
                        List<User> users = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, plugin.twitchClient.getMessagingInterface().getChatters(plugin.chat).execute().getAllViewers()).execute().getUsers();
                        for (User key : users) {
                            synchronized (plugin.viewerPoints) {
                                Integer points = plugin.viewerPoints.get(key.getId());
                                plugin.viewerPoints.put(key.getId(), points + 50);
                                plugin.getServer().getLogger().info("add " + points + " to " + user.getDisplayName() + " now has " + (points + 50));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e){
        Material type = e.getBlock().getType();
        if(plugin.disableShit) {
            if (type == Material.WATER || type == Material.HAY_BLOCK || type == Material.BLACK_BED
                    || type == Material.BLUE_BED || type == Material.BROWN_BED || type == Material.LIGHT_BLUE_BED
                    || type == Material.CYAN_BED || type == Material.GRAY_BED || type == Material.GREEN_BED
                    || type == Material.LIGHT_GRAY_BED || type == Material.LIME_BED || type == Material.MAGENTA_BED
                    || type == Material.ORANGE_BED || type == Material.PINK_BED || type == Material.PURPLE_BED
                    || type == Material.WHITE_BED || type == Material.YELLOW_BED || type == Material.SLIME_BLOCK
                    || type == Material.HONEY_BLOCK || type == Material.LADDER || type == Material.VINE
                    || type == Material.TWISTING_VINES || type == Material.WEEPING_VINES || type == Material.BIRCH_BOAT
                    || type == Material.ACACIA_BOAT || type == Material.OAK_BOAT || type == Material.DARK_OAK_BOAT
                    || type == Material.JUNGLE_BOAT || type == Material.SPRUCE_BOAT) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e){
        if(!plugin.friendlyTeam.hasEntity(e.getPlayer())){
            plugin.friendlyTeam.addPlayer(e.getPlayer());
        }
    }
}
