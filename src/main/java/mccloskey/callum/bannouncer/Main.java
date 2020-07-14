package mccloskey.callum.bannouncer;

import mccloskey.callum.bannouncer.cmd.AnnouncerCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

	private static Main instance;
	private static Announcer bAnnouncer;
	
	public void onEnable() {
		instance = this;
		bAnnouncer = new Announcer();
		
		Objects.requireNonNull(getCommand("bannouncer")).setExecutor(new AnnouncerCommand());
	}
	
	public void onDisable() {
		instance = null;
		bAnnouncer.stop();
		
		saveConfig();
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static Announcer getBAnnouncer() {
		return bAnnouncer;
	}
}
