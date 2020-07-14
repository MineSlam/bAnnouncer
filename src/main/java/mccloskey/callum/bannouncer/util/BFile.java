package mccloskey.callum.bannouncer.util;

import mccloskey.callum.bannouncer.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BFile {

    private final File file;
    private final YamlConfiguration yaml = new YamlConfiguration();

    public BFile(String path) {
        file = new File(path);
        load();
    }

    public void createFile() {
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    Main.getInstance().getLogger().info(file.getName() + " created!");
                }
            } catch (IOException ignored) {}
        }
    }

    public void load() {
        try {
            yaml.load(file);
        } catch (Exception ignored) {}
    }

    public void save() {
        try {
            yaml.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getInt(String s) {
        return yaml.getInt(s);
    }

    public boolean getBoolean(String s) {
        return yaml.getBoolean(s);
    }

    public void add(String s, Object o) {
        if (!contains(s)) {
            set(s, o);
            save();
        }
    }

    public List<String> getStringList(String s) {
        return yaml.getStringList(s);
    }

    public boolean contains(String s) {
        return yaml.contains(s);
    }

    public void set(String s, Object o) {
        yaml.set(s, o);
        save();
    }

    public ConfigurationSection getConfigurationSection(String s) {
        return yaml.getConfigurationSection(s);
    }

    public boolean doesFileExist() {
        return !file.exists();
    }

}
