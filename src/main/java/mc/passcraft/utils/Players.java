package mc.passcraft.utils;

import org.bukkit.entity.Player;
import mc.passcraft.utils.reflection.MethodsUtil;

import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Players {

    public static InetSocketAddress getAddress(Player player) throws InvocationTargetException, IllegalAccessException {
        if(!(player.getClass().getName().equals(Player.class.getName())))
            return MethodsUtil.invoke(player, "getAddress");

        return player.getAddress();
    }

    public static String getIp(Player player) throws InvocationTargetException, IllegalAccessException {
        return getAddress(player).getAddress().getHostAddress();
    }

    //

    public static UUID getOfflineUUID(String player_name) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + player_name).getBytes(StandardCharsets.UTF_8));
    }

}
