package net.sakuragame.eternal.justquest.core.mission.sub;

import com.google.gson.JsonObject;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justalchemy.api.event.SmelterFinishedEvent;
import net.sakuragame.eternal.justalchemy.data.FlameType;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.List;
import java.util.UUID;

public class SmelterMission extends AbstractMission {

    private final int type;
    private final int count;

    public SmelterMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.type = section.getInt("type");
        this.count = section.getInt("count");
    }

    @Override
    public String getPlugin() {
        return "JustAlchemy";
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        SmelterProgress progress = (SmelterProgress) this.getData(uuid);

        String flameName = this.type == -1 ? "任意火焰" : FlameType.match(this.type).getName();
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(
                        new LabelComp("require", "&f熔炼次数: " + (this.count - progress.getCount()) + "/" + this.count +
                                "\n&f要求火焰: &a" + flameName)
                                .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        String flameName = this.type == -1 ? "任意火焰" : FlameType.match(this.type).getName();
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(
                        new LabelComp("require", "&f熔炼次数: " + this.count + "/" + this.count +
                                "\n&f要求火焰: &a" + flameName)
                                .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new SmelterProgress(uuid, questID, String.valueOf(type), count);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new SmelterProgress(uuid, questID, data);
    }

    @EventHandler
    public void onFinished(SmelterFinishedEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (this.type != -1 && e.getType().getID() != this.type) return;
        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        progress.push(String.valueOf(e.getType().getID()));
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    public static class SmelterProgress extends AbstractProgress {

        private final String type;
        private int count;

        public SmelterProgress(UUID uuid, String questID, String type, int count) {
            super(uuid, questID);
            this.type = type;
            this.count = count;
        }

        public SmelterProgress(UUID uuid, String questID, String data) {
            super(uuid, questID);
            JsonObject object = Utils.parse(data);
            this.type = object.get("type").getAsString();
            this.count = object.get("count").getAsInt();
        }

        public int getCount() {
            return count;
        }

        @Override
        public void push() {

        }

        @Override
        public void push(String key) {
            this.push(key, 1);
        }

        @Override
        public void push(String key, int i) {
            if (!key.equals("-1") && !this.type.equals(key)) return;
            this.count--;
        }

        @Override
        public boolean isFinished() {
            return this.count <= 0;
        }

        @Override
        public String getConvertData() {
            JsonObject object = new JsonObject();
            object.addProperty("type", this.type);
            object.addProperty("count", this.count);

            return object.toString();
        }
    }
}
