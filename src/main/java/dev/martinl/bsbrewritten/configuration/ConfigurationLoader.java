package dev.martinl.bsbrewritten.configuration;

import dev.martinl.bsbrewritten.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

@RequiredArgsConstructor
public class ConfigurationLoader<T extends IDeepCloneable> {
    private final Plugin plugin;
    private final String configName;
    private final T defaultConfig;

    private T configData;

    private File configFile;
    private FileConfiguration bukkitPluginConfig;

    public void loadConfiguration() {
        configData = (T) defaultConfig.clone();

        if(!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        configFile = new File(plugin.getDataFolder(), configName);
        try {
            if(!configFile.exists()) {
                configFile.createNewFile();
            }
            writeMissingConfigFields();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        readConfiguration();
    }

    public void writeMissingConfigFields() throws IOException {
        bukkitPluginConfig = new YamlConfiguration();
        for(Field field : defaultConfig.getClass().getFields()) {
            try {
                bukkitPluginConfig.set(StringUtil.convertToSnakeCase(field.getName()), field.get(defaultConfig));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        bukkitPluginConfig.save(configFile);
    }

    private void readConfiguration() {
        bukkitPluginConfig = YamlConfiguration.loadConfiguration(configFile);
    }



}
