package net.sakuragame.eternal.justquest.listener;

import net.sakuragame.eternal.dragoncore.api.CoreAPI;
import net.sakuragame.eternal.dragoncore.api.KeyPressEvent;
import net.sakuragame.eternal.dragoncore.api.event.YamlSendFinishedEvent;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestListener implements Listener {

    public QuestListener() {
        CoreAPI.registerKey("N");
    }

    @EventHandler
    public void onUpdate(YamlSendFinishedEvent e) {
        Player player = e.getPlayer();

        Scheduler.runLaterAsync(() -> {
            if (player.isOnline()) {
                QuestAccount account = JustQuest.getAccountManager().getAccount(player);
                account.updateTraceBar();
            }
        }, 10);
    }

    @EventHandler
    public void onNavigation(KeyPressEvent e) {
        Player player = e.getPlayer();
        if (!e.getKey().equalsIgnoreCase("N")) return;

        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        if (account == null) return;

        IMission mission = account.getTraceMission();
        if (mission == null) return;

        mission.navigation(player);
    }
}
