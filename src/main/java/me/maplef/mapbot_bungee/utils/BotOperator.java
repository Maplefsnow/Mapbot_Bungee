package me.maplef.mapbot_bungee.utils;

import me.maplef.mapbot_bungee.Main;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.BotConfiguration;
import net.md_5.bungee.config.Configuration;

import java.util.Objects;

public class BotOperator {
    private static Bot bot;

    static Configuration config = Main.instance.getConfig();
    static Configuration messages = Main.instance.getMessageConfig();
    private static final long opGroup = config.getLong("op-group");

    public static void login(Long QQ, String password) {
        bot = BotFactory.INSTANCE.newBot(QQ, password, new BotConfiguration(){{
            noNetworkLog();
            noBotLog();
        }});
        bot.login();
    }

    public static void close(){
        send(opGroup, messages.getString("disable-message.group"));
        bot.close();
    }

    public static void send(Long groupID, MessageChain message){
        try{
            Objects.requireNonNull(bot.getGroup(groupID)).sendMessage(message);
        } catch (NullPointerException ignored){}
    }

    public static void send(Long groupID, String message){
        try{
            Objects.requireNonNull(bot.getGroup(groupID)).sendMessage(message);
        } catch (NullPointerException ignored){}
    }

    public static void send(Long groupID, Message message){
        try{
            Objects.requireNonNull(bot.getGroup(groupID)).sendMessage(message);
        } catch (NullPointerException ignored){}
    }

    public static Bot getBot(){
        return bot;
    }
}
