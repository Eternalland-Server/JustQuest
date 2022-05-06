package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justlevel.api.JustLevelAPI;
import net.sakuragame.eternal.justlevel.api.event.PlayerRealmChangeEvent;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.EmptyProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class ElevateRealmMission extends AbstractMission {

    private final int requirement;

    public ElevateRealmMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.requirement = section.getInt("realm", 999);
    }

    @Override
    public void active(UUID uuid, String questID) {
        super.active(uuid, questID);

        if (JustLevelAPI.getRealm(uuid) >= this.requirement) {
            this.complete(uuid);
        }
    }

    @Override
    public void keep(UUID uuid, String questID) {
        super.keep(uuid, questID);

        if (JustLevelAPI.getRealm(uuid) >= this.requirement) {
            this.complete(uuid);
        }
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        int current = JustLevelAPI.getRealm(uuid);

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "当前境界等级: " + current)
                        .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        int current = JustLevelAPI.getRealm(uuid);

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "当前境界等级: " + current)
                        .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new EmptyProgress(uuid, questID);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new EmptyProgress(uuid, questID);
    }

    @EventHandler
    public void onRealmChange(PlayerRealmChangeEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        IProgress progress = this.getData(uuid);
        if (progress == null || progress.isFinished()) return;

        if (e.getRealm() < this.requirement) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }
}
