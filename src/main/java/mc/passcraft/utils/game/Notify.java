package mc.passcraft.utils.game;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Notify {

    public static void chat(Player player, String message, Sound sound) {
        chat(player, message, sound, 1, 1);
    }

    public static void chat(Player player, String message, Sound sound, int volume, int pitch) {
        chat(player, message == null ? null : new TextComponent(message), sound, volume, pitch);
    }

    public static void chat(Player player, BaseComponent message, Sound sound) {
        chat(player, message, sound, 1, 1);
    }

    public static void chat(Player player, BaseComponent message, Sound sound, int volume, int pitch) {
        if(sound != null)
            player.playSound(player.getLocation(), sound, volume, pitch);

        if(message != null)
            player.spigot().sendMessage(message);
    }

    //

    public static void chatSuccess(Player player, String message) { chat(player, message, Sound.ENTITY_EXPERIENCE_ORB_PICKUP); }
    public static void chatSuccess(Player player, BaseComponent message) { chat(player, message, Sound.ENTITY_EXPERIENCE_ORB_PICKUP); }

    public static void chatError(Player player, String message) { chat(player, message, Sound.BLOCK_NOTE_BLOCK_BASS); }
    public static void chatError(Player player, BaseComponent message) { chat(player, message, Sound.BLOCK_NOTE_BLOCK_BASS); }

}
