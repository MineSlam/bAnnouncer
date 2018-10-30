package mccloskey.callum.bannouncer;

import org.bukkit.command.CommandExecutor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import mccloskey.callum.bannouncer.cmd.AnnouncerCommand;
import mccloskey.callum.bannouncer.cmd.ToggleCommand;

public class Main extends JavaPlugin implements Listener {

	private static Main instance;
	private static Announcer bAnnouncer;
	
	public void onEnable() {
		instance = this;
		bAnnouncer = new Announcer();
		
		new AnnouncerCommand();
		new ToggleCommand();
		
		getServer().getPluginManager().registerEvents(this, this);
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
	
	// <3 
	@EventHandler
	public void onDeveloperJoin(PlayerJoinEvent event) {
		String uuid = event.getPlayer().getUniqueId().toString();
		if (uuid.equalsIgnoreCase("b61bbc90-7791-4338-a455-fff021aaa321") || uuid.equalsIgnoreCase("73f1e53f-8373-409f-9554-cf44dce250fa")) {
			event.getPlayer().sendMessage(" ");
			event.getPlayer().sendMessage("§fCurrently running §bbAnnouncer §9v" + getDescription().getVersion() + "§f!");
			event.getPlayer().sendMessage(" ");
		}
	}
}
