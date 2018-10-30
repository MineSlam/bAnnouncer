package mccloskey.callum.bannouncer.cmd;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import mccloskey.callum.bannouncer.Main;
import mccloskey.callum.bannouncer.util.BFile;

public class ToggleCommand implements CommandExecutor {

	private BFile data;
	
	public ToggleCommand() {
		Main.registerCommand(this, "toggleannouncements");
		
		data = new BFile("plugins/bAnnouncer/toggled.yml/");
		if (!data.doesFileExist()) {
			data.createFile();
			data.add("users", Lists.newArrayList());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cOnly players may use this command!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if (!player.hasPermission("bannouncer.ta")) {
			player.sendMessage("§cYou don't have permission to use this command.");
			return true;
		}
		
		List<String> users = data.getStringList("users");
		String uuid = player.getUniqueId().toString();
		
		if (!users.contains(uuid)) {
			users.add(uuid);
		} else {
			users.remove(uuid);
		}
		
		data.set("users", users);
		
		player.sendMessage("§7You have " + (!users.contains(uuid) ? "§aenabled" : "§cdisabled") + " §7your announcements.");
		return false;
	}
}
