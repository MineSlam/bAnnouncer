package mccloskey.callum.bannouncer;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import mccloskey.callum.bannouncer.util.BFile;
import mccloskey.callum.bannouncer.util.Util;
import me.clip.placeholderapi.PlaceholderAPI;

public class Announcer {

	private BFile config, toggled;
	private BukkitTask task;
	
	private List<String> messageIDs, centeredIDs, broadcastFormat, toggledUsers;
	private HashMap<String, List<Sound>> soundMap;
	private HashMap<World, List<String>> worldMap;

	private boolean isRunning, isRandom, isBroadcastCentered, useClips, useMVdW;
	private int interval, current; 
	
	public Announcer() {
		load();
		start();
	}
	
	public void load() {
		config = new BFile("plugins/bAnnouncer/config.yml/");
		if (!config.doesFileExist()) {
			System.out.println("[bAnnouncer] [ERROR] Could not locate configuration file! Loading default configuration file.");
			Main.getInstance().saveDefaultConfig();
			config = new BFile("plugins/bAnnouncer/config.yml/");
		}
		toggled = new BFile("plugins/bAnnouncer/toggled.yml/");
		
		if (!toggled.doesFileExist()) {
			toggled.createFile();
			toggled.add("users", Lists.newArrayList());
		}
		
		toggledUsers = toggled.getStringList("users");
		
		interval = config.getInt("Interval"); 
		isRandom = config.getBoolean("Random");
		
		broadcastFormat = config.getStringList("Broadcast.format");
		isBroadcastCentered = config.getBoolean("Broadcast.centered");
		
		useClips = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
		useMVdW = Bukkit.getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");
		
		messageIDs = Lists.newArrayList();
		centeredIDs = Lists.newArrayList();
		
		soundMap = new HashMap<String, List<Sound>>();
		worldMap = new HashMap<World, List<String>>();
		
		for (String id : config.getConfigurationSection("Messages").getKeys(false)) {
			messageIDs.add(id.toLowerCase());
		}
		
		if (config.contains("Centered-messages")) {
			if (!config.getStringList("Centered-messages").isEmpty()) {
				for (String id : config.getStringList("Centered-messages")) {
					centeredIDs.add(id.toLowerCase());
				}
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

		
		if (config.getConfigurationSection("World-messaging") != null) {
			for (String worldName : config.getConfigurationSection("World-messaging").getKeys(false)) {
				List<String> ids = config.getStringList("World-messaging." + worldName);
				
				World world = Bukkit.getWorld(worldName);
				
				if (world == null) {
					System.out.println("[bAnnouncer] World '" + worldName + "' does not exist.");
					return;
				}
				
				if (!ids.isEmpty()) 
					worldMap.put(world, ids);
			}
		}
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
				if (!isRunning) {
					task.cancel();
					return;
				}
				if (Bukkit.getOnlinePlayers().size() == 0) {
					// do nothing 
					return;
				}
				if (isRandom) {
					// prevent having 2 or more messages appear after each other.
					int rand = new Random().nextInt(messageIDs.size());
					do {
						rand = new Random().nextInt(messageIDs.size());
					} while (current == rand);
					
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
		}
		start();
	}

	public void announceMessage(String id) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			if (!isToggled(player)) {
				if (!worldMap.isEmpty()) {
					for (Entry<World, List<String>> entry : worldMap.entrySet()) {
						if (entry.getValue().contains(id) && !player.getWorld().equals(entry.getKey())) {
							return;
						}
					}
				}
				if (!soundMap.isEmpty()) {
					if (soundMap.containsKey(id)) {
						for (Sound s : soundMap.get(id)) {
							player.playSound(player.getLocation(), s, 1, 1);
						}
					}
				}
				if (centeredIDs.contains(id)) {
					for (String line : config.getStringList("Messages." + id)) {
						line = line.replace("&", "§");
						
						if (useClips) {
							line = PlaceholderAPI.setPlaceholders(player, line);
						} 
						
						if (useMVdW) {
							line = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, line);
						}
						
						Util.sendCenteredMessage(player, line);
					}
				} else {
					for (String line : config.getStringList("Messages." + id)) {
						line = line.replace("&", "§");
						
						if (useClips) {
							line = PlaceholderAPI.setPlaceholders(player, line);
						} 
						
						if (useMVdW) {
							line = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, line);
						}
						
						player.sendMessage(line);
					}
				}
			}
		}
	}

	public void sendMesssage(Player player, String id) {
		if (!isToggled(player)) {
			if (!worldMap.isEmpty()) {
				for (Entry<World, List<String>> entry : worldMap.entrySet()) {
					if (entry.getValue().contains(id) && !player.getWorld().equals(entry.getKey())) {
						return;
					}
				}
			}
			if (!soundMap.isEmpty()) {
				if (soundMap.containsKey(id)) {
					for (Sound s : soundMap.get(id)) {
						player.playSound(player.getLocation(), s, 1, 1);
					}
				}
			}
			if (centeredIDs.contains(id)) {
				for (String line : config.getStringList("Messages." + id)) {
					line = line.replace("&", "§");
					
					if (useClips) {
						line = PlaceholderAPI.setPlaceholders(player, line);
					} 
					
					if (useMVdW) {
						line = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, line);
					}
					
					Util.sendCenteredMessage(player, line);
				}
			} else {
				for (String line : config.getStringList("Messages." + id)) {
					line = line.replace("&", "§");

					if (useClips) {
						line = PlaceholderAPI.setPlaceholders(player, line);
					}

					if (useMVdW) {
						line = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, line);
					}

					player.sendMessage(line);
				}
			}	
		}
	}
	
	public void broadcast(String text) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			for (String line : broadcastFormat) {
				if (line.contains("%text%") && text.contains("<nl>")) {
					if (isBroadcastCentered) {
						for (String part : text.split("<nl>")) {
							line = line.replace("&", "§");

							if (useClips) {
								part = PlaceholderAPI.setPlaceholders(player, part);
							}

							if (useMVdW) {
								part = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, part);
							}
								
							Util.sendCenteredMessage(player, line.replace("%text%", part.replace("&", "§")));	
						}
					} else {
						for (String part : text.split("<nl>")) {
							line = line.replace("&", "§");

							if (useClips) {
								part = PlaceholderAPI.setPlaceholders(player, part);
							}

							if (useMVdW) {
								part = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, part);
							}
							
							player.sendMessage(line.replace("%text%", part.replace("&", "§")));	
						}
					}
				} else {
					if (isBroadcastCentered) {
						line = line.replace("&", "§");

						if (useClips) {
							text = PlaceholderAPI.setPlaceholders(player, text);
						}

						if (useMVdW) {
							text = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, text);
						}
							
						Util.sendCenteredMessage(player, line.replace("%text%", text.replace("&", "§")));	
					} else {
						line = line.replace("&", "§");

						if (useClips) {
							text = PlaceholderAPI.setPlaceholders(player, text);
						}

						if (useMVdW) {
							text = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, text);
						}
						
						player.sendMessage(line.replace("%text%", text.replace("&", "§")));	
					}
				}
			}
		}
	}
	
	public void printMessage(String id) {
		for (String line : config.getStringList("Messages." + id)) {
			System.out.println(line.replace("&", "§"));
		}
	}
	
	public List<String> getMessageIDs() {
		return messageIDs;
	}
	
	public List<String> getCenteredIDs() {
		return centeredIDs;
	}
	
	public BFile getConfig() {
		return config;
	}
	
	public BukkitTask getTask() {
		return task;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	public boolean isRandom() {
		return isRandom;
	}
	
	/**
	 * @return Is server using Clip's PlaceholderAPI
	 */
	public boolean isUsingPAPI() {
		return useClips;
	}
	
	/**
	 * @return Is server using MVdWPlaceholderAPI
	 */
	public boolean isUsingMVdWPlaceholderAPI() {
		return useMVdW;
	}
	
	public boolean isBroadcastCentered() {
		return isBroadcastCentered;
	}
	
	public int getInterval() {
		return interval;
	}
	
	public boolean isToggled(Player player) {
		return toggledUsers.contains(player.getUniqueId().toString());
	}
	
	public void setToggled(Player player, boolean toggle) {
		if (toggle && !isToggled(player)) {
			toggledUsers.add(player.getUniqueId().toString());
		} else if (!toggle && isToggled(player)){
			toggledUsers.remove(player.getUniqueId().toString());
		}
		
		toggled.set("users", toggledUsers);
	}
	
	public String getExact(String id) {
		for (String xid : getMessageIDs()) {
			if (id.equalsIgnoreCase(xid)) {
				return xid;
			}
		}
		
		return null;
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
		if (isRandom == rand) {
			return;
		}
		isRandom = rand;
		config.set("Random", isRandom);
	}
	
	public void setCentered(String id) {
		if (!isValidCID(id)) {
			centeredIDs.add(id);
			config.set("Centered-messages", centeredIDs);
		}
	}
	
	public void removeMessage(String id) {
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
		if (centeredIDs.contains(id)) {
			centeredIDs.remove(id);
			config.set("Centered-messages", centeredIDs);
		}
	}
	
	public void addSound(String id, Sound sound) {
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
	
	public HashMap<World, List<String>> getWorldMap() {
		return worldMap;
	}
	
	public int getCurrent() {
		return current;
	}
}
