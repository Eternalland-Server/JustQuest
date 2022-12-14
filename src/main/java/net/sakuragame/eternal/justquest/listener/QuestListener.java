package net.sakuragame.eternal.justquest.listener;

import net.sakuragame.eternal.dragoncore.api.CoreAPI;
import net.sakuragame.eternal.dragoncore.api.KeyPressEvent;
import net.sakuragame.eternal.dragoncore.api.event.YamlSendFinishedEvent;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justmessage.api.MessageAPI;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.QuestEvent;
import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.util.Scheduler;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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
        }, 5);
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

        if (quest.isSilent()) return;

        PacketSender.sendPlaySound(player, "sounds/q/100.ogg", 0.33f, 1, false, 0, 0, 0);
        Utils.sendNotify(player, "&6&l[!] &f&l????????????", quest.getName(player.getUniqueId()));
    }

    @EventHandler
    public void onCompleted(QuestEvent.Completed e) {
        Player player = e.getPlayer();
        IQuest quest = e.getQuest();
        this.updateCount(e.getPlayer());

        if (quest.getType() != QuestType.CQ) {
            MessageAPI.sendInformMessage(player, quest.getType().getSymbol() + " &a<&f" + quest.getName() + "&a> &7?????????");
        }
        PacketSender.sendPlaySound(player, "sounds/q/101.ogg", 0.33f, 1, false, 0, 0, 0);
    }

    @EventHandler
    public void onFinished(QuestEvent.Finished e) {
        this.updateCount(e.getPlayer());
    }

    private void updateCount(Player player) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        account.sendCompletedCount();
    }
}
