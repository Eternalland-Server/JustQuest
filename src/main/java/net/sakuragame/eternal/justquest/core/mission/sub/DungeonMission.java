package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.CountProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.kirradungeon.client.KirraDungeonClientAPI;
import net.sakuragame.eternal.kirradungeon.common.KirraDungeonCommonAPI;
import net.sakuragame.eternal.kirradungeon.common.event.DungeonClearEvent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class DungeonMission extends AbstractMission {

    private final String id;
    private final int count;

    public DungeonMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.id = section.getString("id");
        this.count = section.getInt("count");
    }

    @Override
    public String getPlugin() {
        return "KirraDungeonCommon";
    }

    @Override
    public void navigation(Player player) {
        if (KirraDungeonCommonAPI.INSTANCE.getCurrentServer() != KirraDungeonCommonAPI.ServerType.CLIENT ) return;
        KirraDungeonClientAPI.INSTANCE.openUI(player, this.id);
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        String name = ChatColor.stripColor(KirraDungeonCommonAPI.INSTANCE.getDisplayNameById(this.id));
        CountProgress progress = (CountProgress) this.getData(uuid);
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp(
                        "require",
                        "⊔&f" + name + ": " + (this.count - progress.getCount()) + "/" + this.count + "\n⊓打开副本界面(N)"
                        )
                        .setExtend("objectives")
                );

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        String name = ChatColor.stripColor(KirraDungeonCommonAPI.INSTANCE.getDisplayNameById(this.id));
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "⊔&f" + name + ": " + this.count + "/" + this.count)
                        .setExtend("objectives")
                );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new CountProgress(uuid, questID, this.count);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new CountProgress(uuid, questID, data);
    }

    @EventHandler
    public void onClear(DungeonClearEvent e) {
        List<Player> players = e.getPlayers();

        for (Player player : players) {
            UUID uuid = player.getUniqueId();
            IProgress progress = this.getData(uuid);
            if (progress == null) return;

            progress.push();
            if (!progress.isFinished()) {
                progress.update();
                return;
            }

            this.complete(uuid);
        }
    }
}
