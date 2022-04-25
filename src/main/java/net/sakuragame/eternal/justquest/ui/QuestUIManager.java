package net.sakuragame.eternal.justquest.ui;

import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import ink.ptms.zaphkiel.ZaphkielAPI;
import lombok.Getter;
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
import net.sakuragame.eternal.justquest.ui.component.QuestList;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestUIManager {

    public final static String CONV_UI_ID = "conv_main";
    public final static String QUEST_UI_ID = "quest_main";
    public final static String QUEST_OBJECTIVE_ID = "quest_objectives";

    @Getter private final Map<UUID, Integer> pageCache;

    public QuestUIManager() {
        this.pageCache = new HashMap<>();
    }

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

    public void turnPage(Player player, int value) {
        UUID uuid = player.getUniqueId();
        if (!this.pageCache.containsKey(uuid)) {
            this.openQuest(player, 0);
            return;
        }

        int page = this.pageCache.get(uuid) + value;
        this.openQuest(player, page);
    }

    public void openQuest(Player player, int page) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);

        PageResult result = Utils.getPagePart(account.getQuests(), page);
        int current = result.getCurrent();
        int total = result.getTotal();
        List<String> quests = result.getList();

        QuestList journal = new QuestList(quests);
        journal.send(player);

        PacketSender.sendRunFunction(player, "default", new Statements()
                .add("global.quest_current_page = " + current + ";")
                .add("global.quest_total_page = " + total + ";")
                .build(),
                false
        );

        if (quests.size() != 0) {
            this.setQuestContent(player, quests.get(0));
        }
        else {
            this.setEmptyContents(player);
        }

        this.pageCache.put(player.getUniqueId(), current);
        PacketSender.sendOpenGui(player, QUEST_UI_ID);
    }

    public void setQuestContent(Player player, String questID) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        QuestProgress progress = account.getQuestProgress().get(questID);

        String missionID = progress.getMissionID();

        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        IMission mission = JustQuest.getProfileManager().getMission(missionID);

        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("quest_title", quest.getName());
        placeholder.put("quest_descriptions", String.join("\n", quest.getDescriptions()));
        placeholder.put("mission_descriptions", String.join("\n", mission.getDescriptions()));
        placeholder.put("quest_reward", quest.getReward().getRewardDescriptions());
        PacketSender.sendSyncPlaceholder(player, placeholder);

        int i = 1;
        for (Map.Entry<String, Integer> entry : quest.getReward().getItems().entrySet()) {
            ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(entry.getKey(), null);
            if (item != null) {
                item.setAmount(entry.getValue());
                PacketSender.putClientSlotItem(player, "quest_reward_" + i, item);
            }
            i++;
        }

        PacketSender.sendRunFunction(player, "default", new Statements()
                .add("global.quest_allow_cancel = " + (quest.isAllowCancel() ? 1 : 0) + ";")
                .add("global.quest_is_completed = " + (progress.isCompleted() ? 1 : 0) + ";")
                .build(),
                false
        );

        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID,
                (progress.isCompleted() ?
                        mission.getCompleteDisplay() :
                        mission.getProgressDisplay(player.getUniqueId())
                ).build(null)
        );
    }

    public void setEmptyContents(Player player) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("quest_descriptions", "");
        placeholder.put("mission_descriptions", "");
        placeholder.put("quest_reward", "");
        PacketSender.sendSyncPlaceholder(player, placeholder);

        ItemStack item = new ItemStack(Material.AIR);
        for (int i = 1; i < 6; i++) {
            PacketSender.putClientSlotItem(player, "quest_reward_" + i, item);
        }

        PacketSender.sendRunFunction(player, "default", new Statements()
                        .add("global.quest_allow_cancel = 0;")
                        .add("global.quest_is_completed = 0;")
                        .build(),
                false
        );

        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, new YamlConfiguration());
    }
}
