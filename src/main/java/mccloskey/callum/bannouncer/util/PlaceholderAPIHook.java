package mccloskey.callum.bannouncer.util;

import mccloskey.callum.bannouncer.Announcer;
import mccloskey.callum.bannouncer.Main;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "bannouncer";
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {

        if (player == null) {
            return null;
        }

        // %bannouncer_toggled%
        if (params.equals("toggled")) {
            Announcer a = Main.getBAnnouncer();
            return a.isToggled(player) ?
                    ChatColor.translateAlternateColorCodes('&',"&4&lOFF") :
                    ChatColor.translateAlternateColorCodes('&',"&a&lON");
        }

        return null;
    }
}
