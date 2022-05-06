package net.sakuragame.eternal.justquest.core.mission.sub;

import com.alibaba.fastjson.JSON;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.juststore.api.event.MerchantTradeEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EquipSuitMission extends AbstractMission {

    private final List<String> suits;

    public EquipSuitMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.suits = section.getStringList("suits");
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        EquipSuitProgress progress = (EquipSuitProgress) this.getData(uuid);
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "套装进度: " + (this.suits.size() - progress.getCount()) + "/" + this.suits.size())
                        .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", "套装进度: " + this.suits.size() + "/" + this.suits.size())
                        .setExtend("objectives")
                );
        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new EquipSuitProgress(uuid, questID, new ArrayList<>(this.suits));
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new EquipSuitProgress(uuid, questID, data);
    }

    @EventHandler
    public void onTrade(MerchantTradeEvent.Post e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String id = e.getGoods().getID();
        if (!this.suits.contains(id)) return;

        IProgress progress = this.getData(uuid);
        if (progress == null || progress.isFinished()) return;

        progress.push(id);
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    private static class EquipSuitProgress extends AbstractProgress {

        private final List<String> suits;

        public EquipSuitProgress(UUID uuid, String questID, List<String> suits) {
            super(uuid, questID);
            this.suits = suits;
        }

        public EquipSuitProgress(UUID uuid, String questID, String data) {
            super(uuid, questID);
            this.suits = JSON.parseArray(data, String.class);
        }

        public int getCount() {
            return this.suits.size();
        }

        @Override
        public void push() {}

        @Override
        public void push(String key) {
            this.suits.remove(key);
        }

        @Override
        public void push(int i) {}

        @Override
        public void push(String key, int i) {}

        @Override
        public boolean isFinished() {
            return this.suits.size() == 0;
        }

        @Override
        public String getConvertData() {
            return JSON.toJSONString(this.suits);
        }
    }
}
