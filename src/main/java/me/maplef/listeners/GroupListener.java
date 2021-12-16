package me.maplef.listeners;

import me.maplef.Main;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.Collection;
import java.util.Map;

public class GroupListener extends SimpleListenerHost{
    Configuration config = Main.getInstance().getConfig();
    Configuration messages = Main.getInstance().getMessageConfig();

    private final long opGroup = config.getLong("op-group");
    private final long playerGroup = config.getLong("player-group");

    @EventHandler
    public void onMessageReceive(GroupMessageEvent e){
        if(e.getGroup().getId() != playerGroup) return;

        String Msg = String.format("<%s> %s", e.getSenderName(), e.getMessage().contentToString());

        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServersCopy();
        for(Map.Entry<String, ServerInfo> entry : servers.entrySet()){
            Collection<ProxiedPlayer> players = entry.getValue().getPlayers();
            for(ProxiedPlayer player : players){
                player.sendMessage(new TextComponent(Msg));
            }
        }
    }
}
