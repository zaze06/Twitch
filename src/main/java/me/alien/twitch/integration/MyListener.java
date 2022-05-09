package me.alien.twitch.integration;

import com.github.twitch4j.helix.domain.User;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathEvent(PlayerDeathEvent e) {
        if (plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.INFO) && e.deathMessage() != null) {
            plugin.twitchClient.getChat().sendMessage(plugin.chat, PaperComponents.plainTextSerializer().serialize(e.deathMessage()));
        }
        if(e.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent e1) {
            if (e1.getDamager() instanceof LivingEntity killer) {
                Component component = killer.customName();
                if (component != null) {
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
                            points = (points == null ? 0 : points);
                            plugin.viewerPoints.put(user.getId(), points + 100);
                            plugin.getServer().getLogger().info("added " + 100 + " to " + user.getDisplayName() + " now has " + (points + 50));
                        }
                    } else {
                        List<User> users = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, plugin.twitchClient.getMessagingInterface().getChatters(plugin.chat).execute().getAllViewers()).execute().getUsers();
                        for (User key : users) {
                            synchronized (plugin.viewerPoints) {
                                Integer points = plugin.viewerPoints.get(key.getId());
                                points = (points == null ? 0 : points);
                                plugin.viewerPoints.put(key.getId(), points + 50);
                                plugin.getServer().getLogger().info("add " + 50 + " to " + key.getDisplayName() + " now has " + (points + 50));
                            }
                        }
                    }
                } else {
                    List<User> users = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, plugin.twitchClient.getMessagingInterface().getChatters(plugin.chat).execute().getAllViewers()).execute().getUsers();
                    for (User key : users) {
                        synchronized (plugin.viewerPoints) {
                            Integer points = plugin.viewerPoints.get(key.getId());
                            points = (points == null ? 0 : points);
                            plugin.viewerPoints.put(key.getId(), points + 50);
                            plugin.getServer().getLogger().info("add " + 50 + " to " + key.getDisplayName() + " now has " + (points + 50));
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttack(EntityDamageByEntityEvent e){
        int d20 = (int)(Math.random()*19)+1;
        if(d20 >= 15){
            int d6 = (int)(Math.random()*5)+1;
            double dmg = e.getDamage();
            double finalDmg = dmg*(0.1*(d6)+1);
            e.setDamage(finalDmg);
            if(e.getDamager() instanceof Player p){
                p.sendMessage(Component.text("<a_twitch_bot_> ").append(Component.text(((int)e.getFinalDamage())+" hp dealt on enemy", TextColor.color(255, 31, 25))));
                //p.sendActionBar(Component.text(((int)e.getFinalDamage())+" hp dealt on enemy", TextColor.color(255, 31, 25)));
                //p.showTitle(Title.title(Component.empty(), Component.text("test"), Title.Times.of(Duration.ofSeconds(1),Duration.ofSeconds(5),Duration.ofSeconds(1))));
                //p.sendTitlePart(TitlePart.SUBTITLE, Component.text(((int)finalDmg)+" dealt on enemy"));
            }
        }else{
            e.setCancelled(true);
        }
    }
}