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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public final class Main extends JavaPlugin {

    private final JSONObject credentials = new JSONObject(Loader.leadFile(getClass().getResourceAsStream("/credentials.json")));
    private final JSONObject redemtions = new JSONObject(Loader.leadFile(getClass().getResourceAsStream("/redemtions.json")));

    private TwitchClient twitchClient;

    private OAuth2Credential credential = new OAuth2Credential("twitch", credentials.getString("user_ID"));
    private CredentialManager credentialManager = CredentialManagerBuilder.builder().build();

    private ArrayList<Action> messageEventAction = new ArrayList<>();
    private ArrayList<Action> reademEventAction = new ArrayList<>();

    private String chat = "";
    private boolean isConnected = false;
    private boolean connectChat = false;

    public static void main(String[] args) {
        Main main = new Main();
        main.onEnable();
    }

    @Override
    public void onEnable() {

        credentialManager.registerIdentityProvider(new TwitchIdentityProvider(credentials.getString("bot_ID"), credentials.getString("bot_Secreat"), ""));

        /*try{
            File file = new File(getConfig().getCurrentPath()+"bot.json");

            if(!file.exists()){
                if(file.exists()){
                    JSONObject data = new JSONObject(Loader.leadFile(new FileInputStream(file)));
                    JSONObject messageEvents = data.getJSONObject("messageEventAction");
                    for (String key : messageEvents.keySet()){
                        JSONObject messageEvent = messageEvents.getJSONObject(key);
                        messageEventAction.add(new Action());
                    }
                }
            }
        }catch (Exception e){

        }*/
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


        Player p = getServer().getOnlinePlayers().toArray(new Player[getServer().getOnlinePlayers().size()])[0];

        if (p == null) {
            return;
        }

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
        } else if (pitch >= 45 && pitch < 135) {
            pos.add(1, 0, 0);
        } else if (pitch >= 135 && pitch < 225) {
            pos.add(0, 0, 1);
        } else if (pitch >= 225 && pitch < 360) {
            pos.add(-1, 0, 0);
        }

        int odds = (int) (Math.random() * 100);

        if (id.equalsIgnoreCase(redemtions.getString("hiss"))) {
            if (odds <= 5) {

                getServer().getWorld("world").spawn(pos, Creeper.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                    e.setSilent(true);
                    e.setPowered(true);
                    e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                });

            } else if (odds <= 40) {
                getServer().getWorld("world").spawn(pos, Creeper.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                    e.setSilent(true);
                    e.customName(Component.text(event.getRedemption().getUser().getDisplayName()));
                });
            }
            //p.sendMessage(event.getRedemption().getUser().getDisplayName()+" redeemed hiss!");
        } else if (id.equalsIgnoreCase(redemtions.getString("BalloonPop"))) {
            if (odds <= 20) {
                for (int x = p.getLocation().getBlockX() - 3; x < p.getLocation().getBlockX() + 4; x++) {
                    for (int y = p.getLocation().getBlockY() - 3; y < p.getLocation().getBlockY() + 4; y++) {
                        for (int z = p.getLocation().getBlockZ() - 3; z < p.getLocation().getBlockZ() + 4; z++) {
                            p.getWorld().setType(x, y, z, Material.AIR);
                        }
                    }
                }
            }
            p.sendMessage(event.getRedemption().getUser().getDisplayName() + " redeemed BalloonPop!");
        } else if (id.equalsIgnoreCase(redemtions.getString("knock"))) {

            p.getWorld().setType(pos, Material.CRIMSON_DOOR);

        } else if (id.equalsIgnoreCase(redemtions.getString("nut"))) {
            if (odds <= 10) {
                int pilliger = ((int) (Math.random() * 2)) + 2;
                int vindicators = ((int) (Math.random() * 2)) + 5;
                int witch = ((int) (Math.random() * 2)) + 1;
                int evoker = 2;
                int ravenderVindicator = ((int) (Math.random() * 1)) + 1;
                int ravangerEvoker = 1;

                int total = pilliger + vindicators + witch + evoker + ravangerEvoker + ravenderVindicator;

                Location location = p.getLocation();

                for (int i = 0; i < total; i++) {
                    int x = (int) (Math.random() * ((location.getBlockX() + 30) - (location.getBlockX() - 30)) + (location.getBlockX() + 30));
                    int z = (int) (Math.random() * ((location.getBlockZ() + 30) - (location.getBlockZ() - 30)) + (location.getBlockZ() + 30));
                    int y = p.getWorld().getMaxHeight();

                    while (p.getWorld().getBlockAt(x, y, z).getType().isAir() && y != p.getWorld().getMinHeight()) {
                        y--;
                    }
                    if (y == p.getWorld().getMinHeight()) {
                        y = p.getLocation().getBlockY();
                    }
                    for (int x1 = x - 1; x1 < x + 1; x1++) {
                        for (int z1 = z - 1; z1 < z + 1; z1++) {
                            Block blockAt = p.getWorld().getBlockAt(x1, y - 1, z1);
                            if (blockAt.getType().isAir()) {
                                blockAt.setType(Material.DIRT);
                            }
                        }
                    }
                    if (pilliger > 0) {
                        getServer().getWorld("world").spawn(new Location(p.getWorld(), x, y, z), Pillager.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                            e.setSilent(true);
                            e.setTarget(p);
                            e.customName(Component.text(userName));
                        });
                        pilliger--;
                    }
                    if (vindicators > 0) {
                        getServer().getWorld("world").spawn(new Location(p.getWorld(), x, y, z), Vindicator.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                            e.setSilent(true);
                            e.setTarget(p);
                            e.customName(Component.text(userName));
                        });
                        vindicators--;
                    }
                    if (witch > 0) {
                        getServer().getWorld("world").spawn(new Location(p.getWorld(), x, y, z), Witch.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                            e.setSilent(true);
                            e.setTarget(p);
                            e.customName(Component.text(userName));
                        });
                        witch--;
                    }
                    if (evoker > 0) {
                        getServer().getWorld("world").spawn(new Location(p.getWorld(), x, y, z), Evoker.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                            e.setSilent(true);
                            e.setTarget(p);
                            e.customName(Component.text(userName));
                        });
                        evoker--;
                    }
                    if (ravangerEvoker > 0) {
                        getServer().getWorld("world").spawn(new Location(p.getWorld(), x, y, z), Ravager.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                            e.setSilent(true);
                            e.setTarget(p);
                            e.customName(Component.text(userName));
                            e.addPassenger(EntityType.EVOKER);
                        });
                        pilliger--;
                    }
                }
            }
        }

        if (cost >= 500) {
            p.playSound(p, Sound.ENTITY_SKELETON_AMBIENT, 10, 10);
            if (odds <= 70) {
                getServer().getWorld("world").spawn(pos, Skeleton.class, CreatureSpawnEvent.SpawnReason.CUSTOM, e -> {
                    e.setSilent(true);
                    e.setTarget(p);
                    e.customName(Component.text(userName));
                });
            }
        }
    }

    private void onChatMessage(ChannelMessageEvent event) {
        if (event.getMessage().equalsIgnoreCase("!test")) {
            //event.reply(twitchClient.getChat(), "yes i work!");
            twitchClient.getChat().sendMessage(chat, "yes i work " + event.getUser().getName());
        } else if (event.getMessage().contains("a_twitch_bot_") && event.getMessage().contains("hi")) {
            twitchClient.getChat().sendMessage(chat, "HI, " + event.getUser());
        } else {
            if (connectChat) {
                getServer().sendMessage(Component.text("<" + event.getUser().getName() + "> " + event.getMessage()));
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (label.equalsIgnoreCase("connect")) {
            if (args.length > 0) {
                twitchClient = TwitchClientBuilder.builder()
                        .withEnableChat(true)
                        .withEnablePubSub(true)
                        .withChatAccount(credential)
                        .withCredentialManager(credentialManager)
                        .build();
                twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, credentials.getString("channel_ID"));
                chat = args[0];
                twitchClient.getChat().joinChannel(chat);
                twitchClient.getChat().sendMessage(chat, "I was told to come hear by " + sender.getName() + " treat me well");
                twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, this::onChatMessage);
                twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, this::onRedemtion);
                sender.getServer().sendMessage(Component.text("Bot connected to " + chat + " stream"));
                isConnected = true;
                return true;
            }
        }
        if (label.equalsIgnoreCase("disconnect")) {
            twitchClient.getChat().sendMessage(chat, "I was told to leave now, so bye!");
            twitchClient.getChat().leaveChannel(chat);
            twitchClient.close();
            isConnected = false;
            sender.getServer().sendMessage(Component.text("Bot disconnected from " + chat + " stream"));
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
        }
        if (label.equalsIgnoreCase("chat")) {
            connectChat = !connectChat;
        }
        return false;
    }
}