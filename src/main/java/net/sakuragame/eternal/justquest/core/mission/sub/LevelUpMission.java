package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justlevel.api.event.PlayerLevelChangeEvent;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.EmptyProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class LevelUpMission extends AbstractMission {

    private final int requirement;

    public LevelUpMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.requirement = section.getInt("level");
    }

    @Override
    public void active(UUID uuid, String questID) {
        super.active(uuid, questID);

        Player player = Bukkit.getPlayer(uuid);
        if (player.getLevel() >= this.requirement) {
            this.complete(uuid);
        }
    }

    @Override
    public void keep(UUID uuid, String questID, String data) {
        super.keep(uuid, questID, data);

        Player player = Bukkit.getPlayer(uuid);
        if (player.getLevel() >= this.requirement) {
            this.complete(uuid);
        }
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "&f达到等级: " + player.getLevel() + "/" + this.requirement)
                        .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "&f达到等级: " + this.requirement + "/" + this.requirement)
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
    public void onLevelChange(PlayerLevelChangeEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        if (e.getNewLevel() < this.requirement) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }
}
