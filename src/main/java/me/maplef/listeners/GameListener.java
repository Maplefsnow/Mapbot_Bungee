package me.maplef.listeners;

import me.maplef.Main;
import me.maplef.utils.BotOperator;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class GameListener implements Listener {
    Configuration config = Main.instance.getConfig();
    Configuration messages = Main.instance.getMessageConfig();

    private final long opGroup = config.getLong("op-group");
    private final long playerGroup = config.getLong("player-group");

    @EventHandler
    public void MessageReceive(ChatEvent e){
        if(e.getMessage().startsWith("/")) return;

        new Thread(() -> {
            ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
            String serverName = messages.getString("server-name." + sender.getServer().getInfo().getName());

            BotOperator.send(opGroup,
                    String.format("[%s]\n", serverName) + String.format("%s: %s", sender.getName(), e.getMessage()));
        }).start();

    }

}
