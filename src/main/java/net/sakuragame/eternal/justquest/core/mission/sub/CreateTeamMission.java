package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.kirraparty.bukkit.event.PartyCreateEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class CreateTeamMission extends AbstractMission {

    public CreateTeamMission(String ID, String type, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, descriptions, section);
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f按 I 键创建队伍")
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new CreateTeamProgress(uuid, questID);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new CreateTeamProgress(uuid, questID);
    }

    @EventHandler
    public void onCreate(PartyCreateEvent e) {
        UUID uuid = e.getLeaderUUID();

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        progress.push("");
        progress.update();
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    public static class CreateTeamProgress extends AbstractProgress {

        private int count;

        public CreateTeamProgress(UUID uuid, String questID) {
            super(uuid, questID);
            this.count = 1;
        }

        @Override
        public void push(String key) {
            this.count--;
        }

        @Override
        public void push(String key, int i) {
            this.count--;
        }

        @Override
        public boolean isFinished() {
            return this.count <= 0;
        }

        @Override
        public String getConvertData() {
            return "" + this.count;
        }
    }
}
