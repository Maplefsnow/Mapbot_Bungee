package me.maplef.mapbot_bungee.managers;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.maplef.mapbot_bungee.Main;
import me.maplef.mapbot_bungee.MapbotPlugin;
import me.maplef.mapbot_bungee.exceptions.CommandNotFoundException;
import me.maplef.mapbot_bungee.exceptions.InvalidSyntaxException;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

public class PluginManager implements Listener {

    public static MessageChain centralHandler(String command, Long groupID, Long senderID , String[] args) throws Exception {
        Reflections reflections = new Reflections("me.maplef.mapbot_bungee.plugins");
        Set<Class<? extends MapbotPlugin>> pluginClasses = reflections.getSubTypesOf(MapbotPlugin.class);

        for(Class<? extends MapbotPlugin> singleClass : pluginClasses){
            Map<String, Object> pluginInfo = (Map<String, Object>) singleClass.getMethod("register").invoke(singleClass.getDeclaredConstructor().newInstance());
            Map<String, Method> pluginCommands = (Map<String, Method>) pluginInfo.get("commands");

            if(pluginCommands.containsKey(command)){
                try{
                    return (MessageChain) pluginCommands.get(command).invoke(singleClass.getDeclaredConstructor().newInstance(), groupID, senderID, args);
                } catch (InvocationTargetException e){
                    MessageChainBuilder errorMsg = new MessageChainBuilder();
                    errorMsg.append(new At(senderID)).append(" ");

                    if(e.getTargetException() instanceof InvalidSyntaxException){
                        Map<String, String> pluginUsage = (Map<String, String>) pluginInfo.get("usages");
                        errorMsg.append(" 语法错误\n用法: ").append(pluginUsage.get(command));
                    } else if(e.getTargetException() instanceof SQLException){
                        errorMsg.append(" 数据库繁忙，请稍后重试");
                        e.getTargetException().printStackTrace();
                    } else {
                        errorMsg.append(e.getTargetException().getMessage());
                    }

                    return errorMsg.build();
                }
            }
        }

        throw new CommandNotFoundException();
    }

    public static void sendOrder(String command, String server, Long groupID, Long senderID , String[] args) throws Exception {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        StringBuilder argStr = new StringBuilder();
        for(String arg : args)
            argStr.append(arg).append(" ");

        out.writeUTF(command);
        out.writeUTF(String.valueOf(groupID));
        out.writeUTF(String.valueOf(senderID));
        out.writeUTF(argStr.toString().trim());

        ServerInfo serverInfo = Main.instance.getProxy().getServerInfo(server);
        if(serverInfo == null) throw new Exception("找不到该服务器，请确保你指定了正确的服务器名");
        serverInfo.sendData("BungeeCord", out.toByteArray());

        System.out.println("Order is sent to " + serverInfo.getName());
    }
}
