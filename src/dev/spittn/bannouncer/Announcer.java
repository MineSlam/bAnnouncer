package dev.spittn.bannouncer;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import dev.spittn.bannouncer.util.SPTNFile;
import dev.spittn.bannouncer.util.Util;
import me.clip.placeholderapi.PlaceholderAPI;

public class Announcer {

	private SPTNFile config;
	private BukkitTask task;
	
	private List<String> messageIDs, centeredIDs;
	private HashMap<String, List<Sound>> soundMap;

	private boolean isRunning;
	private int interval, current; 
	private boolean random;
	
	public Announcer() {
		config = new SPTNFile("plugins/bAnnouncer/config.yml/");
		if (!config.doesFileExist()) {
			System.out.println("[bAnnouncer] Could not locate configuration file!");
			Main.getInstance().saveDefaultConfig();
			return;
		}
		
		interval = config.getInt("Interval"); 
		random = config.getBoolean("Random");
		
		messageIDs = Lists.newArrayList();
		centeredIDs = Lists.newArrayList();
		
		soundMap = new HashMap<String, List<Sound>>();
		
		for (String id : config.getConfigurationSection("Messages").getKeys(false)) {
			messageIDs.add(id.toLowerCase());
		}
		
		if (!config.getStringList("Centered-messages").isEmpty()) {
			for (String id : config.getStringList("Centered-messages")) {
				centeredIDs.add(id.toLowerCase());
			}
		}

		if (config.getConfigurationSection("Sounds") != null) {
			for (String id : config.getConfigurationSection("Sounds").getKeys(false)) {
				List<String> soundNames = config.getStringList("Sounds." + id);

				if (!soundNames.isEmpty()) {
					List<Sound> sounds = Lists.newArrayList();
					for (String name : soundNames) {
						for (Sound sound : Sound.values()) {
							if (name.equalsIgnoreCase(sound.name())) {
								sounds.add(sound);
							}
						}
					}
					
					if (!sounds.isEmpty())
						soundMap.put(id, sounds);
				}
			}
		}

		start();
	}

	public boolean start() {
		if (isRunning) {
			return false;
		}
		isRunning = true;
		current = 0;
		task = new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!isRunning && Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
					task.cancel();
					return;
				}
				if (random) {
					// prevent having 2 or more messages appear after each other.
					int rand = new Random().nextInt(messageIDs.size());
					do {
						rand = new Random().nextInt(messageIDs.size());
					} while(current == rand);
					
					current = rand;
					announceMessage(messageIDs.get(current));
				} else {
					if (current >= messageIDs.size()) {
						current = 0;
					}
					announceMessage(messageIDs.get(current));
					current++;
				}
			}
		}.runTaskTimer(Main.getInstance(), 20, interval* 20);
		return true;
	}
	
	public boolean stop() {
		if (!isRunning) {
			return false;
		}
		isRunning = false;
		if (task != null) {
			if (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) {
				task.cancel();
			}
		}
		return true;
	}
	
	public void restart() {
		if (isRunning) {
			stop();
			start();
		} else {
			start();
		}
	}

	public void announceMessage(String id) {
		id = id.toLowerCase();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!soundMap.isEmpty()) {
				if (soundMap.containsKey(id)) {
					for (Sound s : soundMap.get(id)) {
						player.playSound(player.getLocation(), s, 1, 1);
					}
				}
			}
			if (centeredIDs.contains(id)) {
				for (String line : config.getStringList("Messages." + id)) {
					Util.sendCenteredMessage(player, PlaceholderAPI.setPlaceholders(player, line).replace("&", "§"));
				}
			} else {
				for (String line : config.getStringList("Messages." + id)) {
					player.sendMessage(PlaceholderAPI.setPlaceholders(player, line).replace("&", "§"));
				}	
			}
		}
	}

	public void sendMesssage(Player player, String id) {
		id = id.toLowerCase();
		if (!soundMap.isEmpty()) {
			if (soundMap.containsKey(id)) {
				for (Sound s : soundMap.get(id)) {
					player.playSound(player.getLocation(), s, 1, 1);
				}
			}
		}
		if (centeredIDs.contains(id)) {
			for (String line : config.getStringList("Messages." + id)) {
				Util.sendCenteredMessage(player, PlaceholderAPI.setPlaceholders(player, line).replace("&", "§"));
			}
		} else {
			for (String line : config.getStringList("Messages." + id)) {
				player.sendMessage(PlaceholderAPI.setPlaceholders(player, line).replace("&", "§"));
			}
		}
	}
	
	public void printMessage(String id) {
		id = id.toLowerCase();
		for (String line : config.getStringList("Messages." + id)) {
			System.out.println(line.replaceAll("&", "§"));
		}
	}
	
	public List<String> getMessageIDs() {
		return messageIDs;
	}
	
	public List<String> getCenteredIDs() {
		return centeredIDs;
	}
	
	public SPTNFile getConfig() {
		return config;
	}
	
	public BukkitTask getTask() {
		return task;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public boolean isRandom() {
		return random;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public boolean isValidID(String id) {
		for (String xid : getMessageIDs()) {
			if (id.equalsIgnoreCase(xid)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isValidCID(String id) {
		for (String xid : getCenteredIDs()) {
			if (id.equalsIgnoreCase(xid)) {
				return true;
			}
		}
		return false;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
		config.set("Interval", interval);
		restart();
	}
	
	public void setRandom(boolean rand) {
		if (random == rand) {
			return;
		}
		random = rand;
		config.set("Random", random);
	}
	
	public void setCentered(String id) {
		id = id.toLowerCase();
		if (!isValidCID(id)) {
			centeredIDs.add(id);
			config.set("Centered-messages", centeredIDs);
		}
	}
	
	public void removeMessage(String id) {
		id = id.toLowerCase();
		if (isValidID(id)) {
			messageIDs.remove(id);
			config.set("Messages." + id, null);
		}
		removeCentered(id);
		if (soundMap.containsKey(id)) {
			config.set("Sounds." + id, null);
			soundMap.remove(id);
		}
	}
	
	public void removeCentered(String id) {
		id = id.toLowerCase();
		if (centeredIDs.contains(id)) {
			centeredIDs.remove(id);
			config.set("Centered-messages", centeredIDs);
		}
	}
	
	public void addSound(String id, Sound sound) {
		id = id.toLowerCase();
		if (isValidID(id)) {
			if (soundMap.containsKey(id)) {
				List<Sound> sounds = soundMap.get(id);
				if (sounds.contains(sound)) {
					return;
				}
				sounds.add(sound);
				soundMap.put(id, sounds);
				
				List<String> soundNames = Lists.newArrayList();
				for (Sound s : sounds) {
					soundNames.add(s.name());
				}
				
				config.set("Sounds." + id, soundNames);
			}
		}
	}
	
	public void remSound(String id, Sound sound) {
		id = id.toLowerCase();
		if (isValidID(id)) {
			if (soundMap.containsKey(id)) {
				List<Sound> sounds = soundMap.get(id);
				if (!sounds.contains(sound)) {
					return;
				}
				sounds.remove(sound);
				soundMap.put(id, sounds);
				
				List<String> soundNames = Lists.newArrayList();
				for (Sound s : sounds) {
					soundNames.add(s.name());
				}
				
				config.set("Sounds." + id, soundNames);
			}
		}
	}
	
	public HashMap<String, List<Sound>> getSoundMap() {
		return soundMap;
	}
	
	public int getCurrent() {
		return current;
	}
}
