package org.onedevman.mc.plugins.passcraft.authentication.types;

import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.types.cache.AliasCache;
import org.onedevman.mc.plugins.passcraft.types.KeyFormatter;
import org.onedevman.mc.plugins.passcraft.utils.Players;

import java.lang.reflect.InvocationTargetException;

public class RecentlyDisconnectedCache extends AliasCache<Player, String, Object> {

    public static class Formatter implements KeyFormatter<Player, String> {

        public static String formatFrom(String player_name, String player_ip) {
            return player_name + ":" + player_ip;
        }

        @Override
        public String format(Player player) {
            try {
                return Formatter.formatFrom(player.getName(), Players.getIp(player));
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public RecentlyDisconnectedCache(long delay) {
        super(new Formatter(), delay);
    }

}
