package net.sakuragame.eternal.justquest.listener;

import net.sakuragame.eternal.dragoncore.api.CoreAPI;
import net.sakuragame.eternal.dragoncore.api.KeyPressEvent;
import net.sakuragame.eternal.dragoncore.api.event.YamlSendFinishedEvent;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.QuestEvent;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class QuestListener implements Listener {

    public QuestListener() {
        CoreAPI.registerKey("J");
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
    public void onQuestUI(KeyPressEvent e) {
        Player player = e.getPlayer();
        if (!e.getKey().equalsIgnoreCase("J")) return;

        JustQuest.getUiManager().openQuest(player);
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

    @EventHandler
    public void onAllot(QuestEvent.Allot e) {
        Player player = e.getPlayer();
        IQuest quest = e.getQuest();

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("quest_notice_title", "&6&l[!] &f新任务");
        placeholder.put("quest_notice_contents", quest.getName());

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendRunFunction(player, "default", "global.quest_notice_visible = 1;", true);
        PacketSender.sendOpenHud(player, "quest_notice");
    }
}
