package org.onedevman.mc.plugins.reflectcraft;

import net.minecraft.server.dedicated.DedicatedServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.onedevman.mc.plugins.passcraft.utils.reflection.MethodsUtil;

import java.lang.reflect.InvocationTargetException;

public class ReflectCraft {

    public static DedicatedServer getNativeServer() throws InvocationTargetException, IllegalAccessException {
        return getNativeServer(Bukkit.getServer());
    }

    public static DedicatedServer getNativeServer(Server server) throws InvocationTargetException, IllegalAccessException {
        return (DedicatedServer) MethodsUtil.invoke(server, "getServer");
    }

}
