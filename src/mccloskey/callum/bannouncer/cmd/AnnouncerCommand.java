package mccloskey.callum.bannouncer.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import mccloskey.callum.bannouncer.Announcer;
import mccloskey.callum.bannouncer.Main;
import mccloskey.callum.bannouncer.util.Util;

public class AnnouncerCommand implements CommandExecutor, TabCompleter {
	
	private Announcer announcer;
	
	public AnnouncerCommand() {
		Main.registerCommand(this, "bannouncer");
		announcer = Main.getbAnnouncer();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!sender.hasPermission("bannouncer.ba")) {
			sender.sendMessage("§cYou don't have permission to use this command.");
			return true;
		}
		
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("list")) {
				StringBuilder builder = new StringBuilder();
				
				int x = 0;
				for (String id : announcer.getMessageIDs()) {
					builder.append("&6" + id + "&8, ");
					x++;
				}
				
				message(sender, false, "&8(&6" + x + "&8) &emessages loaded.", "&8[&eMessageIDs&8] &l» " + builder.toString().substring(0, builder.length() - ", ".length()));
				return true;
			}
			if (args[0].equalsIgnoreCase("reload")) {
				Main.getInstance().reloadConfig();
				announcer.load();
				message(sender, false, "&eYou have reloaded the configuration!");
				return true;
			}
			if (args[0].equalsIgnoreCase("setrandom")) {
				message(sender, false, "&eYou have not chosen a valid option: &6Yes &eor &6No", " &8/&eba setrandom &6<y/n>");
				return true;
			}
			if (args[0].equalsIgnoreCase("setinterval")) {
				message(sender, false, "&eYou have not selected an interval.", " &8/&eba setinterval &6<interval>");
				return true;
			}
			if (args[0].equalsIgnoreCase("delmessage")) {
				message(sender, false, "&eYou have not selected a valid messageID to delete.", " &8/&eba delmessage &6<id>");
				return true;
			}
			if (args[0].equalsIgnoreCase("setcentered")) {
				message(sender, false, "&eYou have not selected a valid messageID and option.", " &8/&eba setcentered &6<id> <y/n>");
				return true;
			}
			if (args[0].equalsIgnoreCase("show")) {
				message(sender, false, "&eYou have not selected a valid messageID.", " &8/&eba show &6<id>");
				return true;
			}
			if (args[0].equalsIgnoreCase("addsound")) {
				message(sender, false, "&eYou have not selected a valid messageID and sound to add.", " &8/&eba addsound &6<id> <sound>");
				return true;
			}
			if (args[0].equalsIgnoreCase("remsound") || args[0].equalsIgnoreCase("delsound")) {
				message(sender, false, "&eYou have not selected a valid messageID and sound to remove.", " &8/&eba remsound &6<id> <sound>");
				return true;
			}
			if (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("bc")) {
				message(sender, false, "&eYou have not entered a message to be broadcasted.", " &8/&eba " + args[0].toLowerCase() + " &6<text>");
				return true;
			}
			help(sender);
			return true;
		}
		
		if ((args.length >= 2) && (args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("bc"))) {
			StringBuilder builder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				builder.append(args[i] + " ");
			}
			announcer.broadcast(builder.toString());
			return true;			
		}
		
		String id = "";
		
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("setrandom")) {
				if (StringUtils.startsWithIgnoreCase(args[1], "y")) {
					announcer.setRandom(true);
					message(sender, false, "&8&l» &eYou have set &6Random &emode on.");
					return true;
				} 
				
				if (StringUtils.startsWithIgnoreCase(args[1], "n")) {
					announcer.setRandom(false);
					message(sender, false, "&8&l» &eYou have set &6Random &emode off.");
					return true;
				} 
				
				message(sender, false, "&eYou have not chosen a valid option: &6Yes &eor &6No");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("setinterval")) {
				int interval = 0;
				try {
					interval = Integer.parseInt(args[1]);
				} catch (NumberFormatException nfe) {
					message(sender, false, "&eThe value you have entered is not a number! &8'&c" + args[1] + "&8'.");
					return true;
				}
				
				announcer.setInterval(interval);
				message(sender, false, "&8&l» &eYou have set the interval to &6" + interval + "&e.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("delmessage")) {
				if (announcer.isValidID(args[1])) {
					id = announcer.getExact(args[1]);
					announcer.removeMessage(id);
					message(sender, false, "&8&l» &eYou have deleted message: &6" + id + "&e.");
				} else {
					message(sender, false, "&eThe value you have entered is not a valid id! &8'&c" + args[1] + "&8'.");
				}
				return true;
			}
			
			if (args[0].equalsIgnoreCase("show")) {
				if (announcer.isValidID(args[1])) {
					id = announcer.getExact(args[1]);
					message(sender, false, "&8&l» &ePrivately showing announcement &6" + id + "&e.");
					if (sender instanceof Player) {
						announcer.sendMesssage(((Player)sender), id);	
					} else {
						announcer.printMessage(id);
					}
				} else {
					message(sender, false, "&eThe value you have entered is not a valid id! &8'&c" + args[1] + "&8'.");
				}
				return true;
			}
			help(sender);
			return true;
		}
		
		if (args.length == 3) {
			if (args[0].equalsIgnoreCase("setcentered")) {
				if (announcer.isValidID(args[1])) {
					id = announcer.getExact(args[1]);
					
					if (StringUtils.startsWithIgnoreCase(args[2], "y")) {
						if (!announcer.isValidCID(id)) {
							announcer.setCentered(id);
							message(sender, false, "&8&l» &eYou have centered the message &6" + id + "&e.");		
							return true;
						}
						message(sender, false, "&8&l» &eThe message &6" + id + "&e is already centered.");
						return true;
					} 
					
					if (StringUtils.startsWithIgnoreCase(args[2], "n")) {
						if (announcer.isValidCID(id)) {
							announcer.removeCentered(id);
							message(sender, false, "&8&l» &eYou have uncentered the message &6" + id + "&e.");
							return true;
						} 
						message(sender, false, "&8&l» &eThe message &6" + id +  "&e is not centered.");
						return true;
					} 
					
					message(sender, false, "&eYou have not chosen a valid option: &6Yes &eor &6No");
					return true;
				} 
				message(sender, false, "&eThe value you have entered is not a valid id! &8'&c" + args[1] + "&8'.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("addsound")) {
				if (announcer.isValidID(args[1])) {
					Sound s = null;
					for (Sound sound : Sound.values()) {
						if (sound.name().equalsIgnoreCase(args[2])) {
							s = sound;
						}
					}
					if (s == null) {
						message(sender, false, "&eThe value you have entered is not a valid sound! &8'&c" + args[2] + "&8'.");
						return true;
					}

					id = announcer.getExact(args[1]);
					
					if (announcer.getSoundMap().containsKey(id)) {
						if (announcer.getSoundMap().get(id).contains(s)) {
							message(sender, false, "&eThe message &6" + id + "&e already has this sound added.");
							return true;
						}
					}
					
					announcer.addSound(id, s);
					message(sender, false,  "&8&l» &eThe sound &6" + args[2].toLowerCase() + "&e has been added to &6" + id + "&e.");
					return true;
				}
				message(sender, false, "&eThe value you have entered is not a valid id! &8'&c" + args[1] + "&8'.");
				return true;
			}
			
			if (args[0].equalsIgnoreCase("remsound") || args[0].equalsIgnoreCase("delsound")) {
				if (announcer.isValidID(args[1])) {
					Sound s = null;
					for (Sound sound : Sound.values()) {
						if (sound.name().equalsIgnoreCase(args[2])) {
							s = sound;
						}
					}
					if (s == null) {
						message(sender, false, "&eThe value you have entered is not a valid sound! &8'&c" + args[2] + "&8'.");
						return true;
					}

					id = announcer.getExact(args[1]);
					
					if (announcer.getSoundMap().containsKey(id)) {
						if (!announcer.getSoundMap().get(id).contains(s)) {
							message(sender, false, "&eThe message &6" + id + "&e does not have this sound added.");
							return true;
						}
					}
					
					announcer.remSound(id, s);
					message(sender, false,  "&8&l» &eThe sound &6" + args[2].toLowerCase() + "&e has been removed from &6" + id + "&e.");
					return true;
				}
				message(sender, false, "&eThe value you have entered is not a valid id! &8'&c" + args[1] + "&8'.");
				return true;
			}
			
			help(sender);
			return true;
		}
		
		help(sender);
		return false;
	}
	

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ba") || cmd.getName().equalsIgnoreCase("bannouncer")) {
			if (args.length == 2) {
				if (!(args[0].equalsIgnoreCase("delmessage") || args[0].equalsIgnoreCase("setcentered") || args[0].equalsIgnoreCase("broadcast") || args[0].equalsIgnoreCase("bc")
						|| args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("addsound") || args[0].equalsIgnoreCase("remsound"))) {
					return null;
				}
				ArrayList<String> ids = new ArrayList<String>();


				if (!args[1].equals("")) {
					for (String id : Main.getbAnnouncer().getMessageIDs()) {
						if (id.toLowerCase().startsWith(args[1].toLowerCase())) {
							ids.add(id);
						}
					}
				}

				else {
					for (String id : Main.getbAnnouncer().getMessageIDs()) {
						ids.add(id);
					}
				}

				Collections.sort(ids);
				
				return ids;
			}
			
			if (args.length == 3) {
				if (!(args[0].equalsIgnoreCase("addsound") || args[0].equalsIgnoreCase("remsound"))) {
					return null;
				}
				ArrayList<String> sounds = new ArrayList<String>();

				if (args[0].equalsIgnoreCase("addsound")) {
					if (!args[2].equals("")) {
						for (Sound sound : Sound.values()) {
							if (sound.name().toLowerCase().startsWith(args[2].toLowerCase())) {
								sounds.add(sound.name().toLowerCase());
							}
						}
					}

					else {
						for (Sound sound : Sound.values()) {
							sounds.add(sound.name().toLowerCase());
						}
					}
				} else if (!Main.getbAnnouncer().getSoundMap().isEmpty() && Main.getbAnnouncer().isValidID(args[1])) {
					if (!args[2].equals("")) {
						for (Entry<String, List<Sound>> entry : Main.getbAnnouncer().getSoundMap().entrySet()) {
							if (entry.getKey().equalsIgnoreCase(args[1])) {
								for (Sound sound : entry.getValue()) {
									if (sound.name().toLowerCase().startsWith(args[2].toLowerCase())) {
										sounds.add(sound.name().toLowerCase());
									}
								}
								break;
							}
						}
					}

					else {
						for (Entry<String, List<Sound>> entry : Main.getbAnnouncer().getSoundMap().entrySet()) {
							if (entry.getKey().equalsIgnoreCase(args[1])) {
								for (Sound sound : entry.getValue()) {
									sounds.add(sound.name().toLowerCase());
								}
								break;
							}
						}
					}
				}

				Collections.sort(sounds);

				return sounds;
			}
		}
		return null;
	}
	
	private void help(CommandSender sender) {
		message(sender, true,
				"&8&m------------------------------------------", 
				"&ebAnnouncer &6v1.2.9 &eby &6Callum McCloskey",
				"&8&m------------------------------------------");
		message(sender, false, 		
				"&8&l»     &8/&eba setrandom &6<y/n> &8- &7Ranomize message order.",
				"&8&l»     &8/&eba setinterval &6<interval> &8- &7Set the interval.",
				"&8&l»     &8/&eba delmessage &6<id> &8- &7Delete unwanted messages.",
				"&8&l»     &8/&eba setcentered &6<id> <y/n> &8- &7Toggle message centering.",
				"&8&l»     &8/&eba addsound &6<id> <sound> &8- &7Add any sound.",
				"&8&l»     &8/&eba remsound &6<id> <sound> &8- &7Remove any sound.",
				"&8&l»     &8/&eba reload &8- &7Reload the config file.",
				"&8&l»     &8/&eba broadcast &6<text> &8- &7Broadcast a message.",
				"&8&l»     &8/&eba show &6<id> &8- &7View a message privately.",
				"&8&l»     &8/&eba list &8- &7Lists all valid message IDs.");
		message(sender, true,
				"&8&m------------------------------------------");
	}
	
	private void message(CommandSender sender, boolean centered, String...text) {
		if (centered) {
			for (String txt : text) {
				Util.sendCenteredMessage(sender, txt.replace("&", "§"));
			}
		} else {
			for (String txt : text) {
				sender.sendMessage(txt.replace("&", "§"));
			}	
		}
	}
}
