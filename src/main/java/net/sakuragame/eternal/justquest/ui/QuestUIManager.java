package net.sakuragame.eternal.justquest.ui;

import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.conversation.Dialogue;
import net.sakuragame.eternal.justquest.core.data.PageResult;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.ui.component.ConvDoll;
import net.sakuragame.eternal.justquest.ui.component.ConvOptions;
import net.sakuragame.eternal.justquest.ui.component.JournalList;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestUIManager {

    public final static String CONV_UI_ID = "conv_main";
    public final static String QUEST_UI_ID = "quest_main";
    public final static String QUEST_OBJECTIVE_ID = "quest_objectives";

    public void sendConvDoll(Player player, UUID uuid, double scale) {
        ConvDoll doll = new ConvDoll(uuid, scale);
        doll.send(player);
    }

    public void openConversation(Player player, String name, Dialogue dialogue, boolean animation) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("conv_doll_name", name);
        placeholder.put("conv_contents_text", String.join("\n", dialogue.getResponse()));
        placeholder.put("conv_animation", animation ? "1" : "0");
        PacketSender.sendSyncPlaceholder(player, placeholder);

        ConvOptions uiOptions = new ConvOptions(dialogue.getOptions());
        uiOptions.send(player);

        PacketSender.sendOpenGui(player, CONV_UI_ID);
    }

    public void openQuest(Player player) {
        this.openQuest(player, 0);
    }

    public void openQuest(Player player, int page) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);

        PageResult result = Utils.getPagePart(account.getQuests(), page);
        int current = result.getCurrent();
        int total = result.getTotal();
        List<String> quests = result.getList();

        JournalList journal = new JournalList(quests);
        journal.send(player);

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("journal_current_page", current + "");
        placeholder.put("journal_total_page", total + "");
        PacketSender.sendSyncPlaceholder(player, placeholder);

        if (quests.size() != 0) {
            this.setQuestContent(player, quests.get(0));
        }

        PacketSender.sendOpenGui(player, QUEST_UI_ID);
    }

    public void setQuestContent(Player player, String questID) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        QuestProgress progress = account.getQuestProgress().get(questID);

        String missionID = progress.getMissionID();

        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        IMission mission = JustQuest.getProfileManager().getMission(missionID);

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("quest_descriptions", String.join("\n", mission.getDescriptions()));
        placeholder.put("quest_allow_cancel", quest.isAllowCancel() ? "1" : "0");
        placeholder.put("quest_complete", progress.isCompleted() ? "1" : "0");
        PacketSender.sendSyncPlaceholder(player, placeholder);

        ScreenUI objectives = mission.getProgressDisplay(player.getUniqueId());
        PacketSender.sendYaml(player, FolderType.Gui, objectives.getID(), objectives.build(null));
    }
}
