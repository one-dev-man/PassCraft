package org.onedevman.mc.plugins.passcraft.utils.synchronization;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.onedevman.mc.plugins.passcraft.PluginMain;

public class Async {

    public static BukkitTask call(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(PluginMain.instance(), runnable);
    }

    public static BukkitTask timeout(Runnable runnable, long timeoutticks) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(PluginMain.instance(), runnable, timeoutticks);
    }

}
