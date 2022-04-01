package alien.twitchIntegration;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;

import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public final class Main extends JavaPlugin {

    private final JSONObject credentials = new JSONObject(Loader.leadFile(getClass().getResourceAsStream("/credentials.json")));
    private final JSONObject redemtions = new JSONObject(Loader.leadFile(getClass().getResourceAsStream("/redemtions.json")));

    public TwitchClient twitchClient;

    private final OAuth2Credential credential = new OAuth2Credential("twitch", credentials.getString("user_ID"));
    private final CredentialManager credentialManager = CredentialManagerBuilder.builder().build();

    private final ArrayList<Action> reademEventAction = new ArrayList<>();

    public String chat = null;
    public boolean isConnected = false;
    public boolean connectChatTwitch = false;
    public boolean connectChatMinecraft = false;
    public Level minecraftChat = Level.ALL;

    public ArrayList<PotionEffectType> potionEffectTypes = new ArrayList<>();

    public int time = 0;

    public Timer timer;
    public boolean disableShit = false;

    public FileConfiguration config = getConfig();

    public static void main(String[] args) {
        Main main = new Main();
        main.onEnable();
    }

    @Override
    public void onEnable() {

        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(credentials.getString("bot_ID"), credentials.getString("bot_Secreat"), ""));

        getServer().getPluginManager().registerEvents(new MyListener(this), this);

        try{
            File file = new File(getDataFolder(),"actions.json");

            BufferedReader in = new BufferedReader(new FileReader(file));
            StringBuilder data = new StringBuilder();
            String tmp = "";
            while ((tmp = in.readLine()) != null){
                data.append(tmp);
            }

            JSONObject redeemData = new JSONObject(data);

            for(String key : redeemData.keySet()){
                JSONObject redemtion = redeemData.getJSONObject(key);

            }

        }catch (JSONException e) {
            getServer().sendMessage(Component.text("You don't have a valid redemption action file", TextColor.color(255,0,0)));
        }
        catch(Exception ignored){

        }

        config.addDefault("ChargedCreeperOdds", 5);
        config.addDefault("CreeperOdds", 40);
        config.addDefault("BalloonPopOdds", 50);
        config.addDefault("KnockKnockOdds", 100);
        config.addDefault("KnockKnockBabyOdds", 20);
        config.addDefault("NutOdds", 20);
        config.addDefault("BooOdds", 70);
        config.addDefault("MissionFailedOdds60s", 40);
        config.addDefault("MissionFailedOdds30s", 100);
        config.addDefault("DropItOdds", 20);
        config.addDefault("NameGenOdds", 100);
        config.addDefault("AraAraOdds", 50);
        config.addDefault("HydrateOdds", 100);
        config.addDefault("Debug", false);
        config.options().copyDefaults(true);
        saveConfig();

        potionEffectTypes.add(PotionEffectType.BLINDNESS);
        potionEffectTypes.add(PotionEffectType.POISON);
        potionEffectTypes.add(PotionEffectType.BAD_OMEN);
        potionEffectTypes.add(PotionEffectType.WITHER);

        timer = new Timer(1000, e -> {
            time--;
            if(time == 0){
                disableShit = false;

                timer.stop();
            }
        });
    }

    @Override
    public void onDisable() {
        if (!chat.equalsIgnoreCase("")) {
            twitchClient.getChat().sendMessage(chat, "I was told to leave now so bye!");
        }
    }

    private void onRedemtion(RewardRedeemedEvent event) {
        String id = event.getRedemption().getReward().getId();
        long cost = event.getRedemption().getReward().getCost();
        String userName = event.getRedemption().getUser().getDisplayName();


        Player player = null;

        try{
            player = getServer().getOnlinePlayers().toArray(new Player[getServer().getOnlinePlayers().size()])[0];
        }catch (IndexOutOfBoundsException ignored){}

        if (player == null) {
            return;
        }
        if(chat == null){
            return;
        }

        final Player p = player;

        int pitch = (int) p.getLocation().getPitch();
        Location pos = p.getLocation().clone();
        if (pitch < 0) {
            pitch = 180 + (-pitch);
        }
        if (pitch == 360) {
            pitch = 0;
        }
        if (pitch >= -45 && pitch < 45) {
            pos.add(0, 0, -1);
        }
        else if (pitch >= 45 && pitch < 135) {
            pos.add(1, 0, 0);
        }
        else if (pitch >= 135 && pitch < 225) {
            pos.add(0, 0, 1);
        }
        else if (pitch >= 225 && pitch < 360) {
            pos.add(-1, 0, 0);
        }

        int odds = (int) (Math.random() * 100);

        System.out.println(odds+"");
        if(config.getBoolean("Debug")){
            twitchClient.getChat().sendPrivateMessage("AlienFromDia", odds+"");
        }

        if (id.equalsIgnoreCase(redemtions.getString("hiss"))) {
            if (odds <= config.getInt("ChargedCreeperOdds")) {
                getServer().getScheduler().runTask(this, () -> {
                    p.getWorld().spawn(pos, Creeper.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                        e.setSilent(true);
                        e.setPowered(true);
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                    });
                });

            } else if (odds <= config.getInt("CreeperOdds")) {

                getServer().getScheduler().runTask(this, () -> {
                    p.getWorld().spawn(pos, Creeper.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                        e.setSilent(true);
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                    });
                });
            }
            //p.sendMessage(event.getRedemption().getUser().getDisplayName()+" redeemed hiss!");
        }
        else if (id.equalsIgnoreCase(redemtions.getString("BalloonPop"))) {
            if (odds <= config.getInt("BalloonPopOdds")) {
                getServer().getScheduler().runTask(this, () -> {
                    for (int x = -3; x < 4; x++) {
                        for (int y = -3; y < 4; y++) {
                            for (int z = -3; z < 4; z++) {
                                p.getWorld().setType(p.getLocation().getBlockX()+x, p.getLocation().getBlockY()+y, p.getLocation().getBlockZ()+z, Material.AIR);
                            }
                        }
                    }
                    int i = (int)(Math.random()*potionEffectTypes.size());
                    p.addPotionEffect(potionEffectTypes.get(i).createEffect(40*20, 4));
                    i = (int)(Math.random()*potionEffectTypes.size());
                    p.addPotionEffect(potionEffectTypes.get(i).createEffect(40*20, 4));
                });
                p.sendMessage(event.getRedemption().getUser().getDisplayName() + " redeemed BalloonPop!");
            }
        }
        else if (id.equalsIgnoreCase(redemtions.getString("knock"))) {
            if (odds <= config.getInt("KnockKnockBabyOdds")){
                getServer().getScheduler().runTask(this, () -> {
                    p.getWorld().setType(pos, Material.CRIMSON_DOOR);
                    p.getWorld().spawn(pos, Zombie.class, e -> {
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                        e.setSilent(true);
                        if (odds <= 30) {
                            e.setBaby();
                        }
                    });
                });
            }
        }
        else if (id.equalsIgnoreCase(redemtions.getString("nut"))) {
            if (odds <= config.getInt("NutOdds")) {
                //List of monsters to spawn
                int pilliger = ((int) (Math.random() * 2)) + 2;
                int vindicators = ((int) (Math.random() * 2)) + 5;
                int witch = ((int) (Math.random() * 2)) + 1;
                int evoker = 2;
                int RavagerVindicator = ((int) (Math.random() * 1)) + 1;
                int RavagerEvoker = 1;

                int total = pilliger + vindicators + witch + evoker + RavagerEvoker + RavagerVindicator;

                Location location = p.getLocation();
// redemtion nut, should spawn some monsters accoring to the list above
                for (int i = 0; i < total; i++) {
                    int x = (int) (Math.random() * ((location.getBlockX() + 30) - (location.getBlockX() - 30)) + (location.getBlockX() + 30));
                    int z = (int) (Math.random() * ((location.getBlockZ() + 30) - (location.getBlockZ() - 30)) + (location.getBlockZ() + 30));
                    int y = p.getWorld().getMaxHeight();

                    while (p.getWorld().getBlockAt(x, y, z).getType().isAir() && y != p.getWorld().getMinHeight()) {
                        y--;
                    }
                    if (y == p.getWorld().getMinHeight()) {
                        y = p.getLocation().getBlockY();
                        for (int x1 = x - 1; x1 < x + 1; x1++) {
                            for (int z1 = z - 1; z1 < z + 1; z1++) {
                                Block blockAt = p.getWorld().getBlockAt(x1, y - 1, z1);
                                if (blockAt.getType().isAir()) {
                                    blockAt.setType(Material.DIRT);
                                }
                            }
                        }
                    }

                    int finalY = y;

                    if (pilliger > 0) {
                        getServer().getScheduler().runTask(this, () -> {
                                    p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Pillager.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                        e.setSilent(true);
                                        e.setTarget(p);
                                        e.customName(Component.text(userName));
                                    });
                            });
                        pilliger--;
                    }
                    else if (vindicators > 0) {
                        getServer().getScheduler().runTask(this, () -> {
                                    p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Vindicator.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                        e.setSilent(true);
                                        e.setTarget(p);
                                        e.customName(Component.text(userName));
                                    });
                                });
                        vindicators--;
                    }
                    else if (witch > 0) {
                        getServer().getScheduler().runTask(this, () -> {
                                    p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Witch.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                        e.setSilent(true);
                                        e.setTarget(p);
                                        e.customName(Component.text(userName));
                                    });
                                });
                        witch--;
                    }
                    else if (evoker > 0) {
                        getServer().getScheduler().runTask(this, () -> {
                                    p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Evoker.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                        e.setSilent(true);
                                        e.setTarget(p);
                                        e.customName(Component.text(userName));
                                    });
                                });
                        evoker--;
                    }
                    else if (RavagerEvoker > 0) {
                        getServer().getScheduler().runTask(this, () -> {
                                    p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Ravager.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                        e.setSilent(true);
                                        e.setTarget(p);
                                        e.customName(Component.text(userName));
                                        e.addPassenger(p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Evoker.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e1 -> {
                                            e.customName(Component.text(userName));
                                            e.setTarget(p);
                                            e.setSilent(true);
                                        }));
                                    });
                                });
                        RavagerEvoker--;
                    }
                    else if(RavagerVindicator > 0){
                        getServer().getScheduler().runTask(this, () -> {
                                    p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Ravager.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                                        e.setSilent(true);
                                        e.setTarget(p);
                                        e.customName(Component.text(userName));
                                        e.addPassenger(p.getWorld().spawn(new Location(p.getWorld(), x, finalY, z), Vindicator.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e1 -> {
                                            e.customName(Component.text(userName));
                                            e.setTarget(p);
                                            e.setSilent(true);
                                        }));
                                    });
                                });
                        RavagerVindicator--;
                    }
                }
            }
        }
        else if(id.equalsIgnoreCase(redemtions.getString("boo"))){
            if(odds <= config.getInt("BooOdds")) {
                getServer().getScheduler().runTask(this, () -> {
                    p.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(20 * 20, 3));
                });
            }
        }
        else if(id.equalsIgnoreCase(redemtions.getString("Mission Failed"))){
            if(odds <= config.getInt("MissionFailedOdds60s")) time = 60;
            else if(odds <= config.getInt("MissionFailedOdds30s")) time = 30;
            disableShit = true;
        }
        else if(id.equalsIgnoreCase(redemtions.getString("Drop it"))){
            if(odds <= config.getInt("DropItOdds")) {
                getServer().getScheduler().runTask(this, () -> {
                    for (int y = p.getLocation().getBlockY() + 4; y >= -60; y--) {
                        for (int x = -2; x <= 2; x++) {
                            for (int z = -2; z <= 2; z++) {
                                p.getWorld().setType(p.getLocation().getBlockX()+x, y, p.getLocation().getBlockZ()+z, Material.AIR);
                            }
                        }
                    }
                });
            }
        }
        else if(id.equalsIgnoreCase(redemtions.getString("Name Generator"))){
            if(odds <= config.getInt("NameGenOdds")) {
                getServer().getScheduler().runTask(this, () -> {
                    p.getWorld().spawn(pos, p.getWorld().getLivingEntities().get((int) (Math.random() * p.getWorld().getLivingEntities().size())).getClass(), CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                        e.customName(Component.text(event.getRedemption().getUserInput()));
                    });
                    p.getWorld().spawn(pos, p.getWorld().getLivingEntities().get((int) (Math.random() * p.getWorld().getLivingEntities().size())).getClass(), CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                        e.customName(Component.text(event.getRedemption().getUserInput()));
                    });
                });
            }
        }
        else if(id.equalsIgnoreCase(redemtions.getString("Ara Ara"))){
            if(odds <= config.getInt("AraAraOdds")){
                getServer().getScheduler().runTask(this, () -> {
                    p.getWorld().spawn(pos, Evoker.class, e -> {
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                    });
                    p.getWorld().spawn(pos, Evoker.class, e -> {
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                    });
                    p.getWorld().spawn(pos, Vindicator.class, e -> {
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                    });
                    p.getWorld().spawn(pos, Vindicator.class, e -> {
                        e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                    });
                    p.addPotionEffect(PotionEffectType.SLOW.createEffect(60*20, 2));
                });
            }
        }
        else if(id.equalsIgnoreCase(redemtions.getString("Hydrate"))){
            if(odds <= config.getInt("HydrateOdds")) {
                getServer().getScheduler().runTask(this, () -> {
                    for (int x = -50; x <= 50; x++) {
                        for (int y = -50; y <= 50; y++) {
                            for (int z = -50; z <= 50; z++) {
                                if (p.getWorld().getType(p.getLocation().getBlockX() + x, p.getLocation().getBlockY() + y, p.getLocation().getBlockZ() + z).isAir()) {
                                    p.getWorld().setType(p.getLocation().getBlockX() + x, p.getLocation().getBlockY() + y, p.getLocation().getBlockZ() + z, Material.WATER);
                                }
                            }
                        }
                    }
                });
            }
        }

        if (cost >= 500) {
            p.playSound(p, Sound.ENTITY_SKELETON_AMBIENT, 10, 10);
            if (odds <= 70) {
                getServer().getScheduler().runTask(this, () -> {
                    p.getWorld().spawn(pos, Skeleton.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                        e.setSilent(true);
                        e.setTarget(p);
                        e.customName(Component.text(userName));
                    });
                });
            }
        }
    }

    private void onChatMessage(ChannelMessageEvent event) {
        if (event.getMessage().equalsIgnoreCase("!test")) {
            //event.reply(twitchClient.getChat(), "yes i work!");
            twitchClient.getChat().sendMessage(chat, "yes i work " + event.getUser().getName());
        }
        else if (event.getMessage().contains("a_twitch_bot_") && event.getMessage().contains("hi")) {
            twitchClient.getChat().sendMessage(chat, "HI, " + event.getUser());
        }
        else if(event.getMessage().startsWith("!source")){
            twitchClient.getChat().sendMessage(chat, "I'm a bot made by @AlienFromDia and my source code is located at https://github.com/zaze06/Twitch");
        }
        else {
            if (connectChatTwitch && !event.getUser().getName().equalsIgnoreCase("StreamElements")) {
                getServer().sendMessage(Component.text("<" + event.getUser().getName() + "> " + event.getMessage()));
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("connect")) {
            if (args.length > 0) {
                if(chat == null) {
                    twitchClient = TwitchClientBuilder.builder()
                            .withEnableChat(true)
                            .withEnablePubSub(true)
                            .withChatAccount(credential)
                            .withCredentialManager(credentialManager)
                            .build();
                    chat = args[0];
                    twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, credentials.getString("channel_ID"));
                    twitchClient.getChat().joinChannel(chat);
                    twitchClient.getChat().sendMessage(chat, "I was told to come hear by " + sender.getName() + " treat me well");
                    twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, this::onChatMessage);
                    twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, this::onRedemtion);
                    sender.getServer().sendMessage(Component.text("Bot connected to " + chat + " stream"));
                    isConnected = true;
                }else{
                    sender.sendMessage("<a_twitch_bot_> I'm already connected to a stream. Use /disconnect first");
                }
                return true;
            }
        }
        if (label.equalsIgnoreCase("disconnect")) {
            if(chat != null) {
                twitchClient.getChat().sendMessage(chat, "I was told to leave now, so bye!");
                twitchClient.getChat().leaveChannel(chat);
                twitchClient.close();
                isConnected = false;
                sender.getServer().sendMessage(Component.text("Bot disconnected from " + chat + " stream"));
                chat = null;
            }else{
                sender.sendMessage("<a_twitch_bot_> I'm not connected to a stream. Use /connect <twitch user>");
            }
            return true;
        }
        if (label.equalsIgnoreCase("send")) {
            if (args.length > 0) {
                if (isConnected) {
                    StringBuilder message = new StringBuilder();

                    for (String arg : args) {
                        message.append(arg).append(" ");
                    }

                    twitchClient.getChat().sendMessage(chat, "<" + sender.getName() + "> " + message);
                    getServer().sendMessage(Component.text("<" + sender.getName() + "> " + message));
                }
            }

            getServer().sendMessage(Component.text("<a_twitch_bot_> missing parameters usage /chat <text to send>"));

            return true;
        }
        if (label.equalsIgnoreCase("chat")) {
            if(args.length > 0){
                if(args[0].equalsIgnoreCase("twitch")){
                    connectChatTwitch = !connectChatTwitch;
                    if(connectChatTwitch) {
                        getServer().sendMessage(Component.text("<a_twitch_bot_> the twitch chat is now connected to minecraft chat"));
                        twitchClient.getChat().sendMessage(chat, "twitch chat is now connected to minecraft chat");
                    }else{
                        getServer().sendMessage(Component.text("<a_twitch_bot_> the twitch chat is now disconnected to minecraft chat"));
                        twitchClient.getChat().sendMessage(chat, "twitch chat is now disconnected to minecraft chat");
                    }
                }
                else if(args[0].equalsIgnoreCase("minecraft")){
                    connectChatMinecraft = !connectChatMinecraft;
                    if(connectChatMinecraft) {
                        getServer().sendMessage(Component.text("<a_twitch_bot_> the minecraft chat is now connected to twitch chat"));
                        twitchClient.getChat().sendMessage(chat, "minecraft chat is now connected to twitch chat");
                    }else{
                        getServer().sendMessage(Component.text("<a_twitch_bot_> the minecraft chat is now disconnected to minetwitchcraft chat"));
                        twitchClient.getChat().sendMessage(chat, "minecraft chat is now disconnected to twitch chat");
                    }
                }
                if(args.length > 1){
                    if(args[1].equalsIgnoreCase("all")){
                        minecraftChat = Level.valueOf(args[1].toUpperCase());
                    }else if(args[1].equalsIgnoreCase("info")){
                        minecraftChat = Level.valueOf(args[1].toUpperCase());
                    }else if(args[1].equalsIgnoreCase("chat")){
                        minecraftChat = Level.valueOf(args[1].toUpperCase());
                    }
                }
            }else{
                sender.sendMessage("<a_twitch_bot_> you ar missing parameters use /chat <twitch/minecraft> [all/info/chat note oly works for mc chat and it defaults to all]");
            }

            return true;
        }
        return false;
    }
}