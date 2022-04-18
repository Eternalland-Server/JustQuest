package net.sakuragame.eternal.justquest.listener;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Scheduler.runLaterAsync(() -> {
            if (player.isOnline()) {
                JustQuest.getAccountManager().loadAccount(uuid);
            }
        }, 10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Scheduler.runAsync(() -> JustQuest.getAccountManager().removeAccount(uuid));
    }
}
