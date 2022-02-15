package me.maplef.mapbot_bungee.listeners;

import me.maplef.mapbot_bungee.Main;
import me.maplef.mapbot_bungee.utils.BotOperator;
import me.maplef.mapbot_bungee.utils.CU;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ClientConnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.Collection;
import java.util.Map;

public class GameListener implements Listener {
    Configuration config = Main.instance.getConfig();
    Configuration messages = Main.instance.getMessageConfig();

    private final long opGroup = config.getLong("op-group");
    private final long playerGroup = config.getLong("player-group");

    @EventHandler
    public void MessageReceive(ChatEvent e){
        if(e.getMessage().startsWith("/")) return;

        ProxiedPlayer sender = (ProxiedPlayer) e.getSender();

        new Thread(() -> {
            String serverName = messages.getString("server-name." + sender.getServer().getInfo().getName());

            BotOperator.send(opGroup,
                    String.format("[%s]\n", serverName) + String.format("%s: %s", sender.getName(), e.getMessage()));
        }).start();

        new Thread(() -> {
            String serverName = messages.getString("server-name." + sender.getServer().getInfo().getName());

            Map<String, ServerInfo> servers = ProxyServer.getInstance().getServersCopy();
            for(Map.Entry<String, ServerInfo> entry : servers.entrySet()){
                if(entry.getValue().getName().equals(sender.getServer().getInfo().getName())) continue;

                Collection<ProxiedPlayer> players = entry.getValue().getPlayers();
                for(ProxiedPlayer player : players){
                    player.sendMessage(new TextComponent(CU.t(String.format("&e[%s] &b<%s> &f%s",
                            serverName, sender.getName(), e.getMessage()))));
                }
            }
        }).start();
    }
}
