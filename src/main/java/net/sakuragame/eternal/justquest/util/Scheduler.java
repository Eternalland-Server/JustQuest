package net.sakuragame.eternal.justquest.util;

import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.Bukkit;

public class Scheduler {
    public static void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(JustQuest.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(JustQuest.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, int tick) {
        Bukkit.getScheduler().runTaskLater(JustQuest.getInstance(), runnable, tick);
    }

    public static void runLaterAsync(Runnable runnable, int tick) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(JustQuest.getInstance(), runnable, tick);
    }
}
