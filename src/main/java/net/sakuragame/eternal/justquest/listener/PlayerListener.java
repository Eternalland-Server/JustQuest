package net.sakuragame.eternal.justquest.listener;

import com.taylorswiftcn.megumi.uifactory.event.screen.UIFScreenOpenEvent;
import net.sakuragame.eternal.dragoncore.api.event.YamlSendFinishedEvent;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.QuestAccountLoadEvent;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
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
                QuestAccountLoadEvent event = new QuestAccountLoadEvent(player, JustQuest.getAccountManager().getAccount(uuid));
                event.call();
            }
        }, 10);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        JustQuest.getConversationManager().cancel(uuid);
        JustQuest.getChainManager().removeCache(uuid);
        Scheduler.runAsync(() -> JustQuest.getAccountManager().removeAccount(uuid));
    }

    @EventHandler
    public void onFinish(UIFScreenOpenEvent e) {
        Player player = e.getPlayer();
        if (!e.getScreenID().equals("quest_hint")) return;

        QuestAccount account = JustQuest.getAccountManager().getAccount(player.getUniqueId());
        if (account == null) return;

        account.sendCompletedCount();
    }
}
