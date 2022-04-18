package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justability.api.event.PowerLevelChangeEvent;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class LearnAbilityMission extends AbstractMission {

    private final int count;

    public LearnAbilityMission(String ID, String type, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, descriptions, section);
        this.count = section.getInt("count");
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        LearnAbilityProgress progress = (LearnAbilityProgress) this.getData(uuid);

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f学习技能: " + (this.count - progress.getCount()) + "/" + this.count)
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay() {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f学习技能: " + this.count + "/" + this.count)
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new LearnAbilityProgress(uuid, questID, this.count);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new LearnAbilityProgress(uuid, questID, data);
    }

    @EventHandler
    public void onChange(PowerLevelChangeEvent.Post e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        int change = e.getChange();
        if (change < 0) return;

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        progress.push("", change);
        progress.update();
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    public static class LearnAbilityProgress extends AbstractProgress {

        private int count;

        public LearnAbilityProgress(UUID uuid, String questID, int count) {
            super(uuid, questID);
            this.count = count;
        }

        public LearnAbilityProgress(UUID uuid, String questID, String data) {
            super(uuid, questID);
            this.count = Integer.parseInt(data);
        }

        public int getCount() {
            return count;
        }

        @Override
        public void push(String key) {
            this.count--;
        }

        @Override
        public void push(String key, int i) {
            this.count -= i;
        }

        @Override
        public boolean isFinished() {
            return this.count <= 0;
        }

        @Override
        public String getConvertData() {
            return "" + count;
        }
    }
}
