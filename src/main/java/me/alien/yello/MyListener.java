/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.alien.yello.custome.combat.Base;
import me.alien.yello.custome.combat.Rarity;
import me.alien.yello.events.RandomEvent;
import me.alien.yello.util.dice.Dice;
import me.alien.yello.util.dice.Dices;
import me.limeglass.streamelements.api.StreamElements;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Array;
import java.util.*;

import static me.alien.yello.Main.*;

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
        for(Pair<TwitchClient, String> pair : twitchClients) {
            String chat = pair.value;
            TwitchClient twitchClient = pair.key;
            if (chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.CHAT)) {
                twitchClient.getChat().sendMessage(chat, "<" + e.getPlayer().getName() + "> " + PlainTextComponentSerializer.plainText().serialize(e.message()));
            }
        }

        for (Player p : plugin.getServer().getOnlinePlayers()){
            String message = PlainTextComponentSerializer.plainText().serialize(e.message());
            if(p.equals(e.getPlayer()) && false){
                p.sendMessage(e.getPlayer().getUniqueId(), message);
            }else{
                List<Pair<UUID, Map<String, Integer>>> stats = new ArrayList<>(Main.stats.stream().filter((pair) -> pair.key.equals(p.getUniqueId())).toList());
                Map<String, Integer> playerStats = stats.get(0).getValue();
                double Int = (dice(Dices.D20) + playerStats.get("int"))/20*100;
                StringBuilder out = new StringBuilder();
                for(char c : message.toCharArray()){
                    int x = rand.nextInt(100);
                    if(x > Int){
                        out.append((char)(rand.nextInt(32,127)));
                    }else{
                        out.append(c);
                    }
                }
                p.sendMessage(e.getPlayer().getUniqueId(), out.toString());
            }
        }
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathEvent(PlayerDeathEvent e) {
        for(Pair<TwitchClient, String> pair : twitchClients) {
            String chat = pair.value;
            TwitchClient twitchClient = pair.key;
            StreamElements instance = null;
            List<Pair<String, StreamElements>> pairList = SEInterfaces.stream().filter(pair1 -> pair1.key.equals(chat)).toList();
            if(!pairList.isEmpty()) {
                Pair<String, StreamElements> pair1 = pairList.get(0);
                instance = pair1.value;
            }

            if(instance == null) return;

            if (isConnected && chat != null && plugin.connectChatMinecraft && (plugin.minecraftChat == Level.ALL || plugin.minecraftChat == Level.INFO) && e.deathMessage() != null) {
                twitchClient.getChat().sendMessage(chat, PlainTextComponentSerializer.plainText().serialize(e.deathMessage()));
            }
            if (e.getPlayer().getLastDamageCause() instanceof EntityDamageByEntityEvent e1 && false) {
                if (e1.getDamager() instanceof LivingEntity killer) {
                    Component component = killer.customName();
                    if (component != null) {
                        String cName = PlainTextComponentSerializer.plainText().serialize(component);
                        User user = null;
                        try {
                            user = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, Collections.singletonList(cName)).execute().getUsers().get(0);
                            plugin.getServer().getLogger().info("found user " + user.getDisplayName());
                        } catch (Exception ignore) {
                        }
                        if (user != null) {
                            instance.addPoints(user.getLogin(), 100);
                            plugin.getServer().getLogger().info("added " + 100 + " to " + user.getDisplayName() + " they now has " + (instance.getUserPoints(user.getLogin()).getCurrentPoints()));
                        } else {
                            List<User> users = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, twitchClient.getMessagingInterface().getChatters(chat).execute().getAllViewers()).execute().getUsers();
                            for (User key : users) {
                                instance.addPoints(key.getLogin(), 50);
                                plugin.getServer().getLogger().info("added " + 50 + " to " + key.getDisplayName() + " they now has " + (instance.getUserPoints(key.getLogin()).getCurrentPoints()));
                            }
                        }
                    } else {
                        List<User> users = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, twitchClient.getMessagingInterface().getChatters(chat).execute().getAllViewers()).execute().getUsers();
                        for (User key : users) {
                            instance.addPoints(key.getLogin(), 50);
                            plugin.getServer().getLogger().info("added " + 50 + " to " + key.getDisplayName() + " they now has " + (instance.getUserPoints(key.getLogin()).getCurrentPoints()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent e){
        Player p = e.getPlayer();
        Material type = e.getBlock().getType();
        List<Pair<UUID, Map<String, Integer>>> stats = new ArrayList<>(Main.stats.stream().filter((pair) -> pair.key.equals(p.getUniqueId())).toList());

        Map<String, Integer> playerStats = stats.get(0).getValue();
        if(     type == Material.DIAMOND_ORE || type == Material.DEEPSLATE_DIAMOND_ORE || type == Material.IRON_ORE ||
                type == Material.DEEPSLATE_IRON_ORE || type == Material.COAL_ORE || type == Material.DEEPSLATE_COAL_ORE ||
                type == Material.REDSTONE_ORE || type == Material.DEEPSLATE_REDSTONE_ORE || type == Material.LAPIS_ORE ||
                type == Material.DEEPSLATE_LAPIS_ORE || type == Material.GOLD_ORE || type == Material.DEEPSLATE_GOLD_ORE ||
                type == Material.COPPER_ORE || type == Material.DEEPSLATE_COPPER_ORE || type == Material.EMERALD_ORE ||
                type == Material.DEEPSLATE_EMERALD_ORE || type == Material.NETHER_GOLD_ORE || type == Material.NETHER_QUARTZ_ORE ||
                type == Material.ANCIENT_DEBRIS){
            int x = (int)(Main.rand.nextDouble()*100);
            if(x+playerStats.get("dex") <= 12){
                e.getBlock().getWorld().createExplosion(e.getBlock().getLocation(), 10, true);
                /*e.getBlock().getWorld().spawn(e.getBlock().getLocation().clone().add(0,1,0), TNTPrimed.class, CreatureSpawnEvent.SpawnReason.CUSTOM, (tnt) ->{
                    tnt.setSilent(true);
                    tnt.customName(MiniMessage.miniMessage().deserialize("<red>"+e.getBlock().getType().name()));
                    tnt.setFuseTicks(0);
                });*/
            }
        }
    }

    @EventHandler
    public void onPlayerAttackEvent(EntityDamageByEntityEvent e){
        ItemStack item = null;
        if((e.getDamager() instanceof Monster monster)){
            item = monster.getEquipment().getItemInMainHand();
        }
        else if(e.getDamager() instanceof Player player){
            item = player.getEquipment().getItemInMainHand();
        }
        if(item == null) return;

    }

    @EventHandler
    public void shareDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player p && setting.get("shared_damage")){
            for(Player p1 : Main.plugin.getServer().getOnlinePlayers()){
                if(p1.equals(p)) continue;
                p1.damage(e.getDamage());
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        double lowestHp = 20;
        if(!setting.get("shared_damage")) return;
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(lowestHp > p.getHealth()){
                lowestHp = p.getHealth();
            }
        }
        e.getPlayer().setHealth(lowestHp);
    }

    @EventHandler
    public void onSleepEvent(PlayerBedEnterEvent e){
        double odds = Main.rand.nextDouble(100);
        if(odds <= 0.5){
            e.getBed().getWorld().createExplosion(e.getBed().getLocation(), 10, true);
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
        double lowestHp = 20;
        for(Player p : plugin.getServer().getOnlinePlayers()){
            if(lowestHp > p.getHealth()){
                lowestHp = p.getHealth();
            }
        }
        e.getPlayer().setHealth(lowestHp);

        if(stats.stream().noneMatch((pair) -> pair.key.equals(e.getPlayer().getUniqueId()))){
            Map<String, Integer> stats = new HashMap<>();
            stats.put("str", toMod(rand.nextInt(20)));
            stats.put("dex", toMod(rand.nextInt(20)));
            stats.put("int", toMod(rand.nextInt(20)));
            stats.put("con", toMod(rand.nextInt(20)));
            stats.put("cha", toMod(rand.nextInt(20)));
            stats.put("wiz", toMod(rand.nextInt(20)));
            Main.stats.add(new Pair<>(e.getPlayer().getUniqueId(), stats));
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAttack(EntityDamageByEntityEvent e){
        int d20 = (int)(Math.random()*19)+1;
        if(e.getDamager() instanceof Player p){
            List<Pair<UUID, Map<String, Integer>>> stats = new ArrayList<>(Main.stats.stream().filter((pair) -> pair.key.equals(p.getUniqueId())).toList());
            if(stats.isEmpty()){
                Map<String, Integer> stats1 = new HashMap<>();
                stats1.put("str", toMod(rand.nextInt(20)));
                stats1.put("dex", toMod(rand.nextInt(20)));
                stats1.put("int", toMod(rand.nextInt(20)));
                stats1.put("con", toMod(rand.nextInt(20)));
                stats1.put("cha", toMod(rand.nextInt(20)));
                stats1.put("wiz", toMod(rand.nextInt(20)));
                Pair<UUID, Map<String, Integer>> pair = new Pair<>(p.getUniqueId(), stats1);
                Main.stats.add(pair);
                stats.add(pair);
            }
            Map<String, Integer> playerStats = stats.get(0).getValue();
            if(d20+playerStats.get("str") >= 15) {
                int d6 = (int) (Math.random() * 5) + 1;
                double dmg = e.getDamage();
                double finalDmg = d6 + ((int)((dmg)/2));
                e.setDamage(finalDmg);
                p.sendMessage(Component.text("<a_twitch_bot_> ").append(Component.text(((int) e.getFinalDamage()) + " hp dealt on enemy", TextColor.color(255, 31, 25))));
                //p.sendActionBar(Component.text(((int)e.getFinalDamage())+" hp dealt on enemy", TextColor.color(255, 31, 25)));
                //p.showTitle(Title.title(Component.empty(), Component.text("test"), Title.Times.of(Duration.ofSeconds(1),Duration.ofSeconds(5),Duration.ofSeconds(1))));
                //p.sendTitlePart(TitlePart.SUBTITLE, Component.text(((int)finalDmg)+" dealt on enemy"));
            }else{
                e.setCancelled(true);
            }
        }
    }

    public static Integer toMod(int stat) {
        int mod = 1;
        mod *= Math.floor((stat-10)/2);
        return mod;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player p){
            List<Pair<UUID, Map<String, Integer>>> stats = new ArrayList<>(Main.stats.stream().filter((pair) -> pair.key.equals(p.getUniqueId())).toList());

            Map<String, Integer> playerStats = stats.get(0).getValue();
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
                if(!(rand.nextInt(20)+playerStats.get("dex") <= e.getDamage()/2)){
                    int savedDmg = 0;
                    if(p.getEquipment().getBoots() != null){
                        ItemMeta itemMeta = p.getEquipment().getBoots().getItemMeta();
                        if(itemMeta.hasEnchant(Enchantment.PROTECTION_FALL)){
                            switch (itemMeta.getEnchantLevel(Enchantment.PROTECTION_FALL)){
                                case 1, 2 -> savedDmg = Dices.D4.roll();
                                case 3, 4 -> savedDmg = Dices.D6.roll();
                            }
                        }
                    }
                    e.setDamage(dice(Dices.D6, Dices.D6, Dices.D6)-savedDmg);

                    if(setting.get("shared_damage")) {
                        for (Player p1 : Main.plugin.getServer().getOnlinePlayers()) {
                            if (p1.equals(p)) continue;
                            p1.damage(e.getDamage());
                        }
                    }
                }else{
                    e.setCancelled(true);
                }
            }
        }
    }

    private double dice(Dice... dices) {
        double resualt = 0;
        for(Dice dice : dices){
            resualt += dice.roll();
        }
        return resualt;
    }

    @EventHandler()
    public void onCraft(CraftItemEvent e){
        Material type = e.getRecipe().getResult().getType();
        HumanEntity p = e.getWhoClicked();

        List<Pair<UUID, Map<String, Integer>>> stats = new ArrayList<>(Main.stats.stream().filter((pair) -> pair.key.equals(p.getUniqueId())).toList());

        Map<String, Integer> playerStats = stats.get(0).getValue();

        double wiz = dice(Dices.D20) + playerStats.get("wiz");
        System.out.println(wiz);
        if(wiz < 5 && Main.setting.get("crafting")){
            ItemStack[] matrix = e.getInventory().getMatrix();
            ArrayList<ItemStack> recipe = new ArrayList<>();
            for(ItemStack stack : matrix){
                if(stack != null){
                    recipe.add(stack);
                }
            }
            int i = rand.nextInt(recipe.size());
            e.setCurrentItem(recipe.get(i));
        }
        type = e.getRecipe().getResult().getType();

        boolean isTool = false;
        for(String name : TOOLS.getJSONObject("WEAPON").keySet()){
            if(type.equals(Material.getMaterial(name))){
                isTool = true;
                break;
            }
        }
        if(!isTool) return;
        if(Base.handle(e.getRecipe().getResult()) == null) return;
        e.getRecipe().getResult().setItemMeta(Base.handle(e.getRecipe().getResult()).getItemMeta());
    }

    @EventHandler
    public void onAnvilUse(PrepareAnvilEvent e){
        ItemStack first = e.getInventory().getFirstItem();
        ItemStack second = e.getInventory().getSecondItem();
        if(first == null || second == null) return;
        int r1 = -1;
        int r2 = -1;
        if((r1 = Base.getRarity(first).getMinRarity()) >= 0 && (r2 = Base.getRarity(second).getMinRarity()) >= 0){
            if(r1 > r2){
                ItemStack result = first.clone();
                result.addEnchantments(second.getEnchantments());
                e.setResult(result);
            }else if(r2 > r1){
                ItemStack result = second.clone();
                result.addEnchantments(first.getEnchantments());
                e.setResult(result);
            }
        }
        else if((r1 = Base.getRarity(first).getMinRarity()) >= 0){
            ItemStack result = first.clone();
            result.addEnchantments(second.getEnchantments());
            e.setResult(result);
        }
        else if((r2 = Base.getRarity(second).getMinRarity()) >= 0){
            ItemStack result = second.clone();
            result.addEnchantments(first.getEnchantments());
            e.setResult(result);
        }
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
        if(setting.get("hard_mobs")) {
            if (e.getEntity() instanceof Skeleton mob) {
                if (rand.nextInt(100) <= 50) {
                    ItemStack stack = mob.getEquipment().getItemInMainHand();
                    stack.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
                }
            } else if (e.getEntity() instanceof Zombie mob) {
                if (rand.nextInt(100) <= 33) {
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 999999999, 2));
                    mob.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999999, 3));
                    ItemStack diaSword = new ItemStack(Material.DIAMOND_SWORD);
                    diaSword.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                    diaSword.addEnchantment(Enchantment.FIRE_ASPECT, 2);
                    mob.getEquipment().setItemInMainHand(diaSword);
                }
            }
        }

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
        if(e.getEntity() instanceof Monster monster){
            ItemStack item = monster.getEquipment().getItemInMainHand();
            if(!item.getType().isAir()){
                Base.handle(item);
                if(item != null){
                    monster.getEquipment().setItemInMainHand(item);
                }
            }
        }
    }

    public void showArgs(){

    }

    private void setHp(LivingEntity e, int hp) {
        Objects.requireNonNull(e.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(hp);
        e.setHealth(hp);
    }
}
