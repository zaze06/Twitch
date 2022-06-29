package me.alien.yello.integration;

import com.github.twitch4j.helix.domain.User;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.alien.yello.integration.custome.combat.Base;
import me.alien.yello.integration.events.RandomEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static me.alien.yello.integration.Main.TOOLS;

public class MyListener implements Listener {

    Main plugin;

    public MyListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChatMessageEvent(AsyncChatEvent e){
        boolean isEventPlayer = false;
        if(RandomEvent.player != null){
            isEventPlayer = e.getPlayer().getName().equals(RandomEvent.player.getName());
        }
        if(isEventPlayer && RandomEvent.isRunning){
            String message = PlainTextComponentSerializer.plainText().serialize(e.message());
            if(message.equalsIgnoreCase("exit")){
                RandomEvent.event.end();
            }
            RandomEvent.addData(message);
            e.setCancelled(true);
            e.message(Component.text(""));
            return;
        }
        if(plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.CHAT)){
            plugin.twitchClient.getChat().sendMessage(plugin.chat, "<"+e.getPlayer().getName()+"> "+ PlainTextComponentSerializer.plainText().serialize(e.message()));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathEvent(PlayerDeathEvent e) {
        if (plugin.isConnected && plugin.chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.INFO) && e.deathMessage() != null) {
            plugin.twitchClient.getChat().sendMessage(plugin.chat, PlainTextComponentSerializer.plainText().serialize(e.deathMessage()));
        }
        if(e.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent e1) {
            if (e1.getDamager() instanceof LivingEntity killer) {
                Component component = killer.customName();
                if (component != null) {
                    String cName = PlainTextComponentSerializer.plainText().serialize(component);
                    User user = null;
                    try {
                        user = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, Collections.singletonList(cName)).execute().getUsers().get(0);
                        plugin.getServer().getLogger().info("found user " + user.getDisplayName());
                    } catch (Exception ignore) {
                    }
                    if (user != null) {
                        plugin.instance.addPoints(user.getLogin(), 100);
                        plugin.getServer().getLogger().info("added " + 100 + " to " + user.getDisplayName() + " they now has " + (plugin.instance.getUserPoints(user.getLogin()).getCurrentPoints()));
                    } else {
                        List<User> users = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, plugin.twitchClient.getMessagingInterface().getChatters(plugin.chat).execute().getAllViewers()).execute().getUsers();
                        for (User key : users) {
                            plugin.instance.addPoints(key.getLogin(), 50);
                            plugin.getServer().getLogger().info("added " + 50 + " to " + key.getDisplayName() + " they now has " + (plugin.instance.getUserPoints(key.getLogin()).getCurrentPoints()));
                        }
                    }
                } else {
                    List<User> users = plugin.twitchClient.getHelix().getUsers(plugin.credential.getAccessToken(), null, plugin.twitchClient.getMessagingInterface().getChatters(plugin.chat).execute().getAllViewers()).execute().getUsers();
                    for (User key : users) {
                        plugin.instance.addPoints(key.getLogin(), 50);
                        plugin.getServer().getLogger().info("added " + 50 + " to " + key.getDisplayName() + " they now has " + (plugin.instance.getUserPoints(key.getLogin()).getCurrentPoints()));
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

    @EventHandler(priority = EventPriority.LOW)
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCraft(CraftItemEvent e){
        Material type = e.getRecipe().getResult().getType();
        boolean isTool = false;
        for(String name : TOOLS.getJSONObject("WEAPON").keySet()){
            if(type.equals(Material.getMaterial(name))){
                isTool = true;
                break;
            }
        }
        if(!isTool) return;
        Base.handle(e.getRecipe().getResult());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemPickUp(PlayerAttemptPickupItemEvent e){
        Material type = e.getItem().getItemStack().getType();
        boolean isTool = false;
        for(String name : TOOLS.getJSONObject("WEAPON").keySet()){
            if(type.equals(Material.getMaterial(name))){
                isTool = true;
                break;
            }
        }
        if(!isTool) return;
        Base.handle(e.getItem().getItemStack());
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e){
        if(e.getEntity() instanceof Phantom phantom){
            long time = phantom.getWorld().getGameTime();
            Random rand = new Random(time);
            int odds = rand.nextInt(100);
            if(odds > 30){
                phantom.setSize(odds/10+3);
                setHp(phantom, odds);
                phantom.setShouldBurnInDay(false);
            }
        }
    }

    private void setHp(LivingEntity e, int hp) {
        Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(hp);
        e.setHealth(hp);
    }
}
