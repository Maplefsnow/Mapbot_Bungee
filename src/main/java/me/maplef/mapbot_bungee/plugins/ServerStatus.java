package me.maplef.mapbot_bungee.plugins;

import me.maplef.mapbot_bungee.Main;
import me.maplef.mapbot_bungee.MapbotPlugin;
import me.maplef.mapbot_bungee.exceptions.NoPermissionException;
import me.maplef.mapbot_bungee.utils.BotOperator;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerStatus implements MapbotPlugin {
    private final Bot bot = BotOperator.getBot();

    private static final Configuration config = Main.getInstance().getConfig();
    private static final Configuration messages = Main.getInstance().getMessageConfig();
    private static final Long opGroup = config.getLong("op-group");

    private final boolean[] isOnline = {false};

    @Override
    public MessageChain onEnable(Long groupID, Long senderID, String[] args) throws Exception {
        if(!Objects.requireNonNull(bot.getGroup(opGroup)).contains(senderID))
            throw new NoPermissionException();

        StringBuilder msg = new StringBuilder();

        Map<String, ServerInfo> servers = ProxyServer.getInstance().getServersCopy();
        for(Map.Entry<String, ServerInfo> entry : servers.entrySet()){
            isOnline[0] = false;
            
                msg.append(messages.getString("server-name." + entry.getValue().getName())).append("服务器在线\n");
        }

        return new MessageChainBuilder().append(msg.toString().trim()).build();
    }

    @Override
    public Map<String, Object> register() throws NoSuchMethodException {
        Map<String, Object> info = new HashMap<>();
        Map<String, Method> commands = new HashMap<>();
        Map<String, String> usages = new HashMap<>();

        commands.put("status", ServerStatus.class.getMethod("onEnable", Long.class, Long.class, String[].class));
        commands.put("状态", ServerStatus.class.getMethod("onEnable", Long.class, Long.class, String[].class));

        usages.put("status", "#status - 查询子服状态");
        usages.put("状态", "#状态 - 查询子服状态");

        info.put("name", "server_status");
        info.put("commands", commands);
        info.put("usages", usages);
        info.put("author", "Maplef");
        info.put("description", "查子服状态");
        info.put("version", "1.0");

        return info;
    }
}
