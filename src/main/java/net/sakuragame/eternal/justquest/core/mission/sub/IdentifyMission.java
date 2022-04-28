package net.sakuragame.eternal.justquest.core.mission.sub;

import com.sakuragame.eternal.justattribute.api.event.smithy.SmithyIdentifyEvent;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.CountProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class IdentifyMission extends AbstractMission {

    private final int count;

    public IdentifyMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.count = section.getInt("count");
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        CountProgress progress = (CountProgress) this.getData(uuid);

        ui
                .addComponent(new LabelComp("require", "&f鉴定装备: " + (this.count - progress.getCount()) + "/" + this.count)
                        .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);

        ui
                .addComponent(new LabelComp("require", "&f鉴定装备: " + this.count + "/" + this.count)
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
    public void onIdentify(SmithyIdentifyEvent e) {
        Player player = e.getPlayer();
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
