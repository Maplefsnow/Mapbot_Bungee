package me.maplef.mapbot_bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import me.maplef.mapbot_bungee.utils.BotOperator;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessageListener implements Listener {
    @EventHandler
    public void onPluginMessageReceive(PluginMessageEvent e){
        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

        Long groupID = Long.parseLong(in.readUTF());
        BotOperator.send(groupID, in.readUTF());
    }
}
