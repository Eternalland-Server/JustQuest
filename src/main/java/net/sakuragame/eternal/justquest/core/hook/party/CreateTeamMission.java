package net.sakuragame.eternal.justquest.core.hook.party;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.CountProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.kirraparty.bukkit.event.PartyCreateEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class CreateTeamMission extends AbstractMission {

    public CreateTeamMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
    }

    @Override
    public String getPlugin() {
        return "KirraParty";
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f创建队伍: 0/1")
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f创建队伍: 1/1")
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new CountProgress(uuid, questID, 1);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new CountProgress(uuid, questID, data);
    }

    @EventHandler
    public void onCreate(PartyCreateEvent e) {
        UUID uuid = e.getLeaderUUID();

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
