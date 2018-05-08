package dev.spittn.bannouncer;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.spittn.bannouncer.cmd.AnnouncerCommand;

public class Main extends JavaPlugin {

	private static Main instance;
	private static Announcer bAnnouncer;
	
	public void onEnable() {
		instance = this;
		bAnnouncer = new Announcer();
		new AnnouncerCommand();
	}
	
	public void onDisable() {
		instance = null;
		bAnnouncer.stop();
		
		saveConfig();
	}
	
	public static void registerCommand(CommandExecutor executor, String label) {
		instance.getCommand(label).setExecutor(executor);
	}
	
	public static void registerListener(Listener listener) {
		instance.getServer().getPluginManager().registerEvents(listener, instance);
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static Announcer getbAnnouncer() {
		return bAnnouncer;
	}
}
