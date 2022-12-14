package net.sakuragame.eternal.justquest.listener;

import com.taylorswiftcn.megumi.uifactory.event.comp.UIFCompSubmitEvent;
import com.taylorswiftcn.megumi.uifactory.generate.function.SubmitParams;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import net.sakuragame.eternal.justquest.ui.OperateCode;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UIListener implements Listener {

    @EventHandler
    public void onSubmit(UIFCompSubmitEvent e) {
        Player player = e.getPlayer();
        if (!e.getScreenID().equals(QuestUIManager.QUEST_UI_ID)) return;

        SubmitParams params = e.getParams();
        OperateCode code = OperateCode.match(params.getParamI(0));
        if (code == null) return;

        switch (code) {
            case Up_Page:
                this.onHandlePage(player, -1);
                return;
            case Next_Page:
                this.onHandlePage(player, 1);
                return;
            case Choose_Quest:
                this.onHandleChoose(player, params.getParam(1));
                return;
            case Receive_Reward:
                this.onHandleReceive(player, params.getParam(1));
                return;
            case Cancel_Quest:
                this.onHandleCancel(player, params.getParam(1));
                return;
            case Trace_Quest:
                this.onHandleTrace(player, params.getParam(1));
        }
    }

    private void onHandlePage(Player player, int value) {
        JustQuest.getUiManager().turnPage(player, value);
    }

    private void onHandleChoose(Player player, String questID) {
        JustQuest.getUiManager().setQuestContent(player, questID);
        PacketSender.sendOpenGui(player, QuestUIManager.QUEST_UI_ID);
    }

    private void onHandleReceive(Player player, String questID) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        if (quest == null) return;

        quest.award(player.getUniqueId());
        this.reopen(player);
        player.sendMessage(ConfigFile.prefix + "????????????????????????!");
    }

    private void onHandleCancel(Player player, String questID) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        if (quest == null) return;

        quest.cancel(player.getUniqueId());
        this.reopen(player);
        player.sendMessage(ConfigFile.prefix + "???????????????!");
    }

    private void reopen(Player player) {
        int page = JustQuest.getUiManager().getPageCache().get(player.getUniqueId());
        JustQuest.getUiManager().openQuest(player, page);
    }

    private void onHandleTrace(Player player, String questID) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        account.setQuestTrace(questID);
        account.updateTraceBar();
    }
}
