package mccloskey.callum.bannouncer.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mccloskey.callum.bannouncer.Announcer;
import mccloskey.callum.bannouncer.Main;

public class ToggleCommand implements CommandExecutor {

	public ToggleCommand() {
		Main.registerCommand(this, "toggleannouncements");
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
		
		Announcer a = Main.getbAnnouncer();
		
		a.setToggled(player, !a.isToggled(player));
		
		player.sendMessage("§7You have " + (!a.isToggled(player) ? "§aenabled" : "§cdisabled") + " §7your announcements.");
		return false;
	}
}
