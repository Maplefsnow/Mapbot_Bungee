package me.maplef.mapbot_bungee;

import me.maplef.mapbot_bungee.listeners.GameListener;
import me.maplef.mapbot_bungee.listeners.GroupListener;
import me.maplef.mapbot_bungee.listeners.PluginMessageListener;
import me.maplef.mapbot_bungee.utils.BotOperator;
import me.maplef.mapbot_bungee.utils.CU;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public final class Main extends Plugin implements Listener {
    public static Main instance;

    private Configuration config;
    private Configuration messages;

    @Override
    public void onEnable() {
        instance = this;

        try{
            if(registerConfig()){
                System.out.println("请在生成的配置文件中修改相关参数，然后重新加载本插件");
                getProxy().getPluginManager().getPlugin("Mapbotbungee").onDisable();
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        new Thread(() -> {
            System.out.println("Mapbot正在登陆，请耐心等待...");
            BotOperator.login(config.getLong("bot-account"), config.getString("bot-password"));
            BotOperator.getBot().getEventChannel().registerListenerHost(new GroupListener());
            BotOperator.send(config.getLong("op-group"), messages.getString("enable-message.group"));
            System.out.println("Mapbot登陆成功");
        }).start();

        this.getProxy().getPluginManager().registerListener(this, new GameListener());
        this.getProxy().getPluginManager().registerListener(this, new PluginMessageListener());

        System.out.println(CU.t(messages.getString("enable-message.console")));
    }

    @Override
    public void onDisable() {
        BotOperator.close();
        System.out.println(CU.t(messages.getString("disable-message.console")));
    }

    public static Main getInstance(){
        return instance;
    }

    public boolean registerConfig() throws IOException {
        boolean isFirstStart = false;

        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
            isFirstStart = true;
        }

        File configFile = new File(getDataFolder(), "config.yml");
        File messageFile = new File(getDataFolder(), "messages.yml");

        if (!configFile.exists()) {
            isFirstStart = true;
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!messageFile.exists()) {
            isFirstStart = true;
            try (InputStream in = getResourceAsStream("messages.yml")) {
                Files.copy(in, messageFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "messages.yml"));

        return isFirstStart;
    }

    public Configuration getConfig(){
        return config;
    }

    public Configuration getMessageConfig(){
        return messages;
    }

}
