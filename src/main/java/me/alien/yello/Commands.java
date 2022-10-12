/*
 * MIT License
 *
 * Copyright (c) 2022. Zacharias Zellén
 */

package me.alien.yello;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import me.alien.yello.events.PrintHandler;
import me.despical.commandframework.Command;
import me.despical.commandframework.CommandArguments;
import me.despical.commandframework.Completer;
import me.limeglass.streamelements.api.StreamElements;
import me.limeglass.streamelements.api.StreamElementsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

import static me.alien.yello.Main.*;
import static me.alien.yello.MyListener.toMod;

public class Commands {
    @Command(name = "connect",
            desc = "Connects to a twitch chat",
            usage = "/connect <twitch user>",
            min = 1)
    public void connectCommand(CommandArguments arguments){
        final String chat = arguments.getArgument(0);
        CommandSender sender = arguments.getSender();

        if (twitchClients.stream().filter(pair -> pair.value.equals(chat)).toList().isEmpty()) {
            TwitchClient twitchClient = TwitchClientBuilder.builder()
                    .withEnableChat(true)
                    .withEnablePubSub(true)
                    .withEnableHelix(true)
                    .withEnableTMI(true)
                    .withChatAccount(credential)
                    .withCredentialManager(credentialManager)
                    .build();

            User user = twitchClient.getHelix().getUsers(credential.getAccessToken(), null, List.of(chat)).execute().getUsers().get(0);
            if(user == null){
                sender.sendMessage("<a_twitch_bot_> I'm sorry but "+chat+" is not a valid twitch user name please make sure you spelled correctly.");
                return;
            }

            twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(credential, user.getId());
            twitchClient.getChat().joinChannel(chat);
            twitchClient.getChat().sendMessage(chat, "I was told to come hear by " + sender.getName() + " treat me well.");
            twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, e -> Main.onChatMessage(e, new Pair<>(twitchClient, chat)));
            twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, e -> Main.onRedemption(e, new Pair<>(twitchClient, chat)));
            sender.getServer().sendMessage(Component.text("<a_twitch_bot_> Bot connected to " + chat + " stream."));
            isConnected = true;
            //host = p;

            if(credentials.getJSONObject("SE").has(chat.toLowerCase())) {
                StreamElements instance = new StreamElementsBuilder()
                        .withAccountID(credentials.getJSONObject("SE").getString(chat.toLowerCase()))
                        .withToken(credentials.getJSONObject("SE").getString("token"))
                        .withConnectionTimeout(10000)
                        .build();
                SEInterfaces.add(new Pair<>(chat, instance));
            }
            if(credentials.getJSONObject("SL").has(chat.toLowerCase())){
                //StreamlabsApi api = StreamlabsApiBuilder.builder()
                //        .withClientId("")
                //        .build();
            }

            twitchClients.add(new Pair<>(twitchClient, chat));
        } else {
            sender.sendMessage("<a_twitch_bot_> I'm already connected to "+chat+"'s stream. Use /disconnect first.");
        }
    }

    @Completer(name = "connect")
    public List<String> connectCommandCompleter(CommandArguments arguments){
        return List.of("<Twitch user>");
    }

    @Command(name = "disconnect",
            desc = "Disconnects for a twitch channel",
            usage = "/disconnect [<Twitch user>]"
    )
    public void disconnectCommand(CommandArguments arguments){
        String[] args = arguments.getArguments();

        if(args.length > 0){
            final String chat = args[0];
            List<Pair<TwitchClient, String>> pairList = twitchClients.stream().filter(pair -> pair.value.equals(chat)).toList();
            if(!pairList.isEmpty()){
                Pair<TwitchClient, String> pair = pairList.get(0);
                TwitchClient twitchClient = pair.key;
                twitchClient.getChat().sendMessage(chat, "I was told to leave now so bye!");
                twitchClient.getChat().disconnect();
                twitchClient.close();
            }
        }else{
            for(Pair<TwitchClient, String> pair : twitchClients){
                pair.key.getChat().sendMessage(pair.value, "I was told to leave now so bye!");
                pair.key.close();
            }
        }
    }

    @Completer(name = "disconnect")
    public List<String> disconnectCommandCompleter(CommandArguments arguments){
        ArrayList<String> args = new ArrayList<>();
        for(Pair<TwitchClient, String> pair : twitchClients){
            args.add(pair.getValue());
        }
        return args;
    }

    @Command(name = "send",
            desc = "Sends a message to the connected twitch channels",
            min = 1
    )
    public void sendCommand(CommandArguments arguments){
        String[] args = arguments.getArguments();
        CommandSender sender = arguments.getSender();
        if(!twitchClients.isEmpty()) {
            for (Pair<TwitchClient, String> twitchPair : twitchClients) {
                TwitchClient twitchClient = twitchPair.key;
                String chat = twitchPair.value;
                StringBuilder message = new StringBuilder();


                for (String arg : args) {
                    message.append(arg).append(" ");
                }

                twitchClient.getChat().sendMessage(chat, "<" + sender.getName() + "> " + message);
                plugin.getServer().sendMessage(Component.text("<" + sender.getName() + "> " + message));
            }
        }else{
            sender.sendMessage(Component.text("<a_twitch_bot_> ").append(MiniMessage.miniMessage().deserialize("<red><bold>Sorry but I'm not connected to a Twitch chat use /connect <twitch streamer>")));
        }
    }

    @Completer(name = "send")
    public List<String> sendCommandCompleter(CommandArguments arguments){
        return List.of("<message>");
    }

    @Command(name = "chat",
            desc = "Enable bi/single direcinal chat",
            min = 1
    )
    public void chatCommand(CommandArguments arguments){
        String[] args = arguments.getArguments();

        if (!twitchClients.isEmpty()) {
            for (Pair<TwitchClient, String> twitchPair : twitchClients) {
                TwitchClient twitchClient = twitchPair.key;
                String chat = twitchPair.value;
                if (args[0].equalsIgnoreCase("twitch")) {
                    connectChatTwitch = !connectChatTwitch;
                    if (connectChatTwitch) {
                        plugin.getServer().sendMessage(Component.text("<a_twitch_bot_> the twitch chat is now connected to minecraft chat"));
                        twitchClient.getChat().sendMessage(chat, "twitch chat is now connected to minecraft chat");
                    } else {
                        plugin.getServer().sendMessage(Component.text("<a_twitch_bot_> the twitch chat is now disconnected to minecraft chat"));
                        twitchClient.getChat().sendMessage(chat, "twitch chat is now disconnected to minecraft chat");
                    }
                } else if (args[0].equalsIgnoreCase("minecraft")) {
                    plugin.connectChatMinecraft = !plugin.connectChatMinecraft;
                    if (plugin.connectChatMinecraft) {
                        plugin.getServer().sendMessage(Component.text("<a_twitch_bot_> the minecraft chat is now connected to twitch chat"));
                        twitchClient.getChat().sendMessage(chat, "minecraft chat is now connected to twitch chat");
                    } else {
                        plugin.getServer().sendMessage(Component.text("<a_twitch_bot_> the minecraft chat is now disconnected to minetwitchcraft chat"));
                        twitchClient.getChat().sendMessage(chat, "minecraft chat is now disconnected to twitch chat");
                    }
                    if (args.length > 1) {
                        if (args[1].equalsIgnoreCase("all")) {
                            plugin.minecraftChat = Level.valueOf(args[1].toUpperCase());
                        } else if (args[1].equalsIgnoreCase("info")) {
                            plugin.minecraftChat = Level.valueOf(args[1].toUpperCase());
                        } else if (args[1].equalsIgnoreCase("chat")) {
                            plugin.minecraftChat = Level.valueOf(args[1].toUpperCase());
                        }
                    }
                }
            }
        }
    }

    @Completer(name = "chat")
    public List<String> chatCommandCompleter(CommandArguments arguments){
        ArrayList<String> head = new ArrayList<>();
        head.add("twitch");
        head.add("minecraft");
        ArrayList<String> mc = new ArrayList<>();
        mc.add("all");
        mc.add("info");
        mc.add("chat");
        ArrayList<String> out = head;
        if(arguments.getArgument(0) != null){
            if(arguments.getArgument(0).equalsIgnoreCase("minecraft")){
                out = mc;
            }else if(arguments.getArgument(0).equalsIgnoreCase("twitch")){
                out = new ArrayList<>();
            }
        }
        return out;
    }

    @Command(name = "grace",
            desc = "Sets the grace time"
    )
    public void graceCommand(CommandArguments arguments){
        String[] args = arguments.getArguments();
        CommandSender sender = arguments.getSender();
        if(args.length > 0){
            try {
                graceTime = Integer.parseInt(args[0])*60;
                graceTimeOrig = graceTime;
                grace = true;
            }catch (Exception e){
                sender.sendMessage("<a_twitch_bot_> first argument was not an int pleas provide <time in min> for the first argument");
            }
        }else{
            sender.sendMessage("<a_twitch_bot_> first argument was not an found pleas provide <time in min> for the first argument");
        }
    }

    @Completer(name = "grace")
    public List<String> graceCommandCompleter(CommandArguments arguments){
        return List.of("<time in minutes>");
    }

    @Command(name = "stat",
            desc = "gives the stat of ether yourself or a friend",
            senderType = Command.SenderType.PLAYER
    )
    public void statsCommand(CommandArguments arguments){


        Player sender = arguments.getSender(), player = !arguments.isArgumentsEmpty() ? plugin.getServer().getPlayer(arguments.getArgument(0)) : sender;

        if(player == null){
            arguments.sendMessage("invalid");
            return;
        }

        //player.sendMessage("test");

        List<Pair<UUID, Map<String, Integer>>> stats = new ArrayList<>(Main.stats.stream().filter((pair) -> pair.key.equals(player.getUniqueId())).toList());
        if(stats.isEmpty()){
            Map<String, Integer> stats1 = new HashMap<>();
            stats1.put("str", toMod(rand.nextInt(20)));
            stats1.put("dex", toMod(rand.nextInt(20)));
            stats1.put("int", toMod(rand.nextInt(20)));
            stats1.put("con", toMod(rand.nextInt(20)));
            stats1.put("cha", toMod(rand.nextInt(20)));
            stats1.put("wiz", toMod(rand.nextInt(20)));
            Pair<UUID, Map<String, Integer>> pair = new Pair<>(player.getUniqueId(), stats1);
            Main.stats.add(pair);
            stats.add(pair);
        }
        PrintHandler out = player::sendMessage;
        for(Map.Entry<String, Integer> pair : stats.get(0).value.entrySet()){
            out.print(pair.getKey() + ": " + pair.getValue());
        }
    }

    @Completer(name = "stat")
    public List<String> statsCommandCompleter(CommandArguments arguments){
        ArrayList<String> playerNames = new ArrayList<>();
        for(Player p : plugin.getServer().getOnlinePlayers()){
            playerNames.add(PlainTextComponentSerializer.plainText().serialize(p.displayName()));
        }
        return playerNames;
    }

    @Command(name = "config",
            min = 2
    )
    public void config(CommandArguments arguments){
        if(arguments.getArguments().length >= 2){
            try {
                setting.replace(arguments.getArgument(0), Boolean.parseBoolean(arguments.getArgument(1)));
                arguments.sendMessage(arguments.getArgument(0)+" is now set to "+arguments.getArgument(1));
            }catch (Exception ignore){
                arguments.sendMessage(arguments.getArgument(0)+" dos not exist");
            }
        }else if(arguments.getArguments().length >= 1){
            try {
                arguments.sendMessage(arguments.getArgument(0)+" is set to "+setting.get(arguments.getArgument(0)));
            }catch (Exception ignore){
                arguments.sendMessage(arguments.getArgument(0)+" dos not exist");
            }
        }
    }

    @Completer(name = "config")
    public ArrayList<String> configCompleter(CommandArguments arguments){
        ArrayList<String> out = new ArrayList<>(setting.keySet().stream().toList());
        if(arguments.getArgument(0) != null){
            if(setting.keySet().stream().toList().contains(arguments.getArgument(0))) {
                out = new ArrayList<>();
                out.add("true");
                out.add("false");
            }
        }
        return out;
    }

    @Command(name = "settings")
    public void settings(CommandArguments arguments){
        for (Map.Entry<String, Boolean> set : setting.entrySet()){
            arguments.sendMessage(set.getKey()+" is set to "+set.getValue());
        }
    }
}
