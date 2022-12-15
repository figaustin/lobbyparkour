package com.etsuni.lobbyparkour;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

public final class LobbyParkour extends JavaPlugin {

    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    private File customConfigFile;
    private FileConfiguration customConfig;

    private static final Logger log = Logger.getLogger("Minecraft");

    protected static LobbyParkour plugin;

    @Override
    public void onEnable() {
        plugin = this;
        createCustomConfig();
        if(!connect()) {
            log.severe(String.format("[%s] - Disabled due to config not setup correctly, please add the correct values! " +
                    "Please change uri to your own uri/connection string!", getDescription().getName()));
            log.severe(String.format("[%s] - If you get another error after setting this up, you have entered info wrong!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getCommand("parkour").setExecutor(new Commands());
        this.getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {


    }

    public Boolean connect() {
        String uri = customConfig.getString("database.uri");
        String databaseName = customConfig.getString("database.database_name");
        String collectionName = customConfig.getString("database.collection_name");

        if(uri == null) {
            return false;
        }
        if(databaseName == null) {
            return false;
        }
        if(collectionName == null) {
            return false;
        }
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase(databaseName);
        collection = database.getCollection(collectionName);
        return true;
    }

    public MongoCollection<Document> getCollection() {
        return collection;
    }

    private void createCustomConfig() {
        customConfigFile = new File(getDataFolder(), "config.yml");
        if(!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        customConfig = new YamlConfiguration();

        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void updateConfig() {
        File config = new File(getDataFolder(), "config.yml");
        YamlConfiguration externalYamlConfig = YamlConfiguration.loadConfiguration(config);
        InputStreamReader defConfigStream = new InputStreamReader(getResource("config.yml"), StandardCharsets.UTF_8);
        YamlConfiguration internalYamlConfig = YamlConfiguration.loadConfiguration(defConfigStream);

        for(String str : internalYamlConfig.getKeys(true)) {
            if(!externalYamlConfig.contains(str)) {
                externalYamlConfig.set(str, internalYamlConfig.get(str));
            }
        }
        try {
            externalYamlConfig.save(config);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void saveCfg() {
        try {
            customConfig.save(customConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FileConfiguration getCustomConfig() {
        return this.customConfig;
    }

    public void setCustomConfig(FileConfiguration fileConfiguration) {
        this.customConfig = fileConfiguration;
    }

    public File getCustomConfigFile() {
        return this.customConfigFile;
    }

}
