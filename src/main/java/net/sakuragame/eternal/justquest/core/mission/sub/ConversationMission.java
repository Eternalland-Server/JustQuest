package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import lombok.Getter;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class ConversationMission extends AbstractMission {

    private final String convID;

    public ConversationMission(String ID, String type, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, descriptions, section);
        this.convID = section.getString("id");
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f完成对话: 0/1")
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay() {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", "&f完成对话: 1/1")
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new ConversationProgress(uuid, questID, this.convID);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new ConversationProgress(uuid, questID, data);
    }

    @EventHandler
    public void onComplete(ConversationEvent.Complete e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String ID = e.getConversation().getID();

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        progress.push(ID);
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    @Getter
    public static class ConversationProgress extends AbstractProgress {

        private final String ID;
        private boolean complete;

        public ConversationProgress(UUID uuid, String questID, String ID) {
            super(uuid, questID);
            this.ID = ID;
            this.complete = false;
        }

        public String getID() {
            return ID;
        }

        @Override
        public void push(String key) {
            if (!key.equals(ID)) return;
            this.complete = true;
        }

        @Override
        public void push(String key, int i) {
            this.push(key);
        }

        @Override
        public boolean isFinished() {
            return this.complete;
        }

        @Override
        public String getConvertData() {
            return this.ID;
        }
    }
}
