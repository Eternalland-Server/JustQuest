package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.dragoncore.api.event.PlayerSlotUpdateEvent;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.EmptyProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class SlotMission extends AbstractMission {

    private final String slotID;

    public SlotMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.slotID = section.getString("id");
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        return new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        return new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
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
    public void onUpdate(PlayerSlotUpdateEvent e) {
        Player player = e.getPlayer();
        String ident = e.getIdentifier();

        IProgress progress = this.getData(player.getUniqueId());
        if (progress == null || progress.isFinished()) return;

        if (!this.slotID.equals(ident)) return;

        this.complete(player.getUniqueId());
    }
}
