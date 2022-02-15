package me.maplef.mapbot_bungee.listeners;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.maplef.mapbot_bungee.Main;
import me.maplef.mapbot_bungee.exceptions.CommandNotFoundException;
import me.maplef.mapbot_bungee.managers.PluginManager;
import me.maplef.mapbot_bungee.utils.BotOperator;
import me.maplef.mapbot_bungee.utils.CU;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MemberJoinEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.GroupedThreadFactory;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.regex.Pattern;

public class GroupListener extends SimpleListenerHost{
    Configuration config = Main.getInstance().getConfig();
    Configuration messages = Main.getInstance().getMessageConfig();

    private final long opGroup = config.getLong("op-group");
    private final long playerGroup = config.getLong("player-group");

    static final LinkedList<Message> messageRecorder = new LinkedList<>();

    private final String commandPattern = "^#[\\u4E00-\\u9FA5A-Za-z0-9_]+(\\s[\\u4E00-\\u9FA5A-Za-z0-9_\\s]+)?$";

    @EventHandler
    public void onCommandReceive(GroupMessageEvent e){
        if(!Pattern.matches(commandPattern, e.getMessage().contentToString())) return;

        ArrayList<String> msgSplit = new ArrayList<>(List.of(e.getMessage().contentToString().split(" ")));
        String command = msgSplit.get(0).substring(1);

        msgSplit.remove(0);

        int size = msgSplit.size();
        String[] args = msgSplit.toArray(new String[size]);

        MessageChainBuilder message = new MessageChainBuilder();

        try {
            message.append(PluginManager.centralHandler(command, e.getGroup().getId(), e.getSender().getId(), args));
        } catch (CommandNotFoundException ex) {
            String server = msgSplit.get(0);
            msgSplit.remove(0);
            args = msgSplit.toArray(new String[size-1]);
            try {
                System.out.println(command);
                System.out.println(server);
                System.out.println(e.getGroup().getId());
                System.out.println(e.getSender().getId());
                System.out.println(Arrays.toString(args));

                PluginManager.sendOrder(command, server, e.getGroup().getId(), e.getSender().getId(), args);
            } catch (Exception exc) {
                message.append(exc.getMessage());
            }

        } catch (Exception ex){
            ex.printStackTrace();
        }

        BotOperator.send(e.getGroup().getId(), message.build());
    }

    @EventHandler
    public void onMessageReceive(GroupMessageEvent e){
        if(e.getGroup().getId() != playerGroup) return;
        if(Pattern.matches(commandPattern, e.getMessage().contentToString())) return;

        String Msg = String.format("[群消息] <%s> %s", e.getSenderName(), e.getMessage().contentToString());

        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServersCopy();
        for(Map.Entry<String, ServerInfo> entry : servers.entrySet()){
            Collection<ProxiedPlayer> players = entry.getValue().getPlayers();
            for(ProxiedPlayer player : players){
                player.sendMessage(new TextComponent(Msg));
            }
        }
    }

    @EventHandler
    public void onRepeat(GroupMessageEvent e){
        if(e.getGroup().getId() != playerGroup) return;
        messageRecorder.push(e.getMessage());
        if(messageRecorder.size() == 3){
            if(messageRecorder.get(0).contentEquals(messageRecorder.get(1), false) &&
                    messageRecorder.get(1).contentEquals(messageRecorder.get(2), false)){
                messageRecorder.clear();
                BotOperator.send(e.getGroup().getId(), e.getMessage());
            }

            messageRecorder.clear();
        }
    }

    @EventHandler
    public void onNewCome(MemberJoinEvent e){
        if(e.getGroupId() != playerGroup) return;

        StringBuilder msg = new StringBuilder();
        for(String singleMsg : messages.getStringList("welcome-new-message.group"))
            msg.append(singleMsg).append("\n");

        BotOperator.send(e.getGroupId(), msg.toString().trim());
        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServersCopy();
        for(Map.Entry<String, ServerInfo> entry : servers.entrySet()){
            Collection<ProxiedPlayer> players = entry.getValue().getPlayers();
            for(ProxiedPlayer player : players){
                player.sendMessage(new TextComponent(CU.t(messages.getString("message-prefix") + messages.getString("welcome-new-message.server"))));
            }
        }
    }

    @EventHandler
    public void onTest(GroupMessageEvent e){
        if(e.getGroup().getId() != opGroup) return;

        if(e.getMessage().contentToString().contains("test")){
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Forward");
            out.writeUTF("main");
            out.writeUTF("onlinetime maplef 3");

            ProxiedPlayer player = Iterables.getFirst(ProxyServer.getInstance().getPlayers(), null);
            if(player == null) {
                BotOperator.send(opGroup, "no player online");
                return;
            }
            player.sendData("BungeeCord", out.toByteArray());

            System.out.println("插件消息已发送！");
        }
    }
}
