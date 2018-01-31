package dev.spittn.bannouncer.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.inventory.ItemStack;

public class SPTNFile {

	private File file = null;
	private YamlConfiguration yaml = new YamlConfiguration();

	public SPTNFile(File file) {
		this.file = file;
		load();
	}

	public SPTNFile(String path) {
		file = new File(path);
		load();
	}

	public void createFile() {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException localIOException) {
			}
		}
	}

	public void load() {
		try {
			yaml.load(file);
		} catch (Exception localException) {
		}
	}

	public void save() {
		try {
			yaml.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		try {
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final File getFile() {
		return file;
	}

	public int getInt(String s) {
		return yaml.getInt(s);
	}

	public void reload() {
		save();
		load();
	}

	public String getString(String s) {
		return yaml.getString(s);
	}

	public Object get(String s) {
		return yaml.get(s);
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

	public void addToStringList(String s, String o) {
		yaml.getStringList(s).add(o);
		save();
	}

	public void removeFromStringList(String s, String o) {
		yaml.getStringList(s).remove(o);
		save();
	}

	public List<String> getStringList(String s) {
		return yaml.getStringList(s);
	}

	public void addToIntegerList(String s, int o) {
		yaml.getIntegerList(s).add(Integer.valueOf(o));
		save();
	}

	public void removeFromIntegerList(String s, int o) {
		yaml.getIntegerList(s).remove(o);
		save();
	}

	public List<Integer> getIntegerList(String s) {
		return yaml.getIntegerList(s);
	}

	public void createNewStringList(String s, List<String> list) {
		yaml.set(s, list);
		save();
	}

	public void createNewIntegerList(String s, List<Integer> list) {
		yaml.set(s, list);
		save();
	}

	public void remove(String s) {
		set(s, null);
		save();
	}

	public boolean contains(String s) {
		return yaml.contains(s);
	}

	public double getDouble(String s) {
		return yaml.getDouble(s);
	}

	public void set(String s, Object o) {
		yaml.set(s, o);
		save();
	}

	public ConfigurationSection getConfigurationSection(String s) {
		return yaml.getConfigurationSection(s);
	}

	public void createConfigurationSection(String s) {
		yaml.createSection(s);
		save();
	}

	public void increment(String s) {
		yaml.set(s, Integer.valueOf(getInt(s) + 1));
		save();
	}

	public void decrement(String s) {
		yaml.set(s, Integer.valueOf(getInt(s) - 1));
		save();
	}

	public void increment(String s, int i) {
		yaml.set(s, Integer.valueOf(getInt(s) + i));
		save();
	}

	public void decrement(String s, int i) {
		yaml.set(s, Integer.valueOf(getInt(s) - i));
		save();
	}

	public YamlConfigurationOptions options() {
		return yaml.options();
	}

	public boolean doesFileExist() {
		if (!file.exists()) {
			return false;
		}
		return true;
	}

	public ItemStack getItemStack(String path) {
		return yaml.getItemStack(path);
	}
}
