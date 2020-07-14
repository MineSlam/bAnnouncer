package mccloskey.callum.bannouncer;

import mccloskey.callum.bannouncer.cmd.AnnouncerCommand;
import mccloskey.callum.bannouncer.util.PlaceholderAPIHook;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    private static Main instance;
    private static Announcer bAnnouncer;

    public void onEnable() {
        instance = this;
        bAnnouncer = new Announcer();

        Objects.requireNonNull(getCommand("bannouncer")).setExecutor(new AnnouncerCommand());

        new PlaceholderAPIHook().register();
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
