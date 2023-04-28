package org.onedevman.mc.plugins.passcraft.authentication.types;

import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.types.cache.AliasCache;
import org.onedevman.mc.plugins.passcraft.types.cache.CachePersistency;
import org.onedevman.mc.plugins.passcraft.utils.Players;

import java.lang.reflect.InvocationTargetException;

public class JoinMessageRegistry extends AliasCache<Player, String, String> {

    public JoinMessageRegistry() {
        super(player -> {
            try {
                return Players.getAddress(player).toString();
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }, CachePersistency.PERSISTENT);
    }

}
