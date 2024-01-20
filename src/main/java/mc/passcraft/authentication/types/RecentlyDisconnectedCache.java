package mc.passcraft.authentication.types;

import org.bukkit.entity.Player;
import mc.passcraft.types.cache.AliasCache;
import mc.passcraft.utils.Players;

import java.lang.reflect.InvocationTargetException;

public class RecentlyDisconnectedCache extends AliasCache<Player, String, Object> {

    public RecentlyDisconnectedCache(long delay) {
        super(player -> {
            try {
                return player.getName() + Players.getIp(player);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, delay);
    }

}
