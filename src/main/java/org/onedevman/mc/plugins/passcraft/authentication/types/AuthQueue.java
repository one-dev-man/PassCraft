package org.onedevman.mc.plugins.passcraft.authentication.types;

import org.bukkit.entity.Player;
import org.onedevman.mc.plugins.passcraft.types.Queue;
import org.onedevman.mc.plugins.passcraft.utils.Players;

import java.lang.reflect.InvocationTargetException;

public class AuthQueue extends Queue<Player, String> {

    public AuthQueue() {
        super(player -> {
            try {
                return Players.getAddress(player).toString();
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
