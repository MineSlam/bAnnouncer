package mccloskey.callum.bannouncer.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
/**
 // * @Author SirSpoodles
 *
 // * @see https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
 */
public class Util {

    private final static int CENTER_PX = 154;

    public static void sendCenteredMessage(CommandSender player, String message) {
        if (message == null || message.equals(""))
            player.sendMessage("");

        assert message != null;
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        player.sendMessage(sb.toString() + message);
    }
}
