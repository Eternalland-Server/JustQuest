package net.sakuragame.eternal.justquest.core.mission.sub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;

public class MobKillerMission extends AbstractMission {

    private final Map<String, Integer> requirement;

    public MobKillerMission(String ID, String type, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, descriptions, section);
        this.requirement = new LinkedHashMap<>();

        for (String key : section.getKeys(false)) {
            int amount = section.getInt(key);
            this.requirement.put(key, amount);
        }
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        List<String> display = new ArrayList<>();
        MobKillerProgress progress = (MobKillerProgress) this.getData(uuid);
        requirement.forEach((k, v) -> {
            MythicMob mob = MythicMobs.inst().getAPIHelper().getMythicMob(k);
            if (mob != null) {
                String name = ChatColor.stripColor(mob.getDisplayName().get());
                display.add("&f" + name + ": " + (progress == null ? v : v - progress.getCount(k)) + "/" + v);
            }
            else {
                display.add(k + ": error");
            }
        });

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui.addComponent(
                new LabelComp("require", String.join("\n", display))
                        .setExtend("objectives")
        );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new MobKillerProgress(uuid, questID, new LinkedHashMap<>(requirement));
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new MobKillerProgress(uuid, questID, data);
    }

    @EventHandler
    public void onDeath(MythicMobDeathEvent e) {
        if (!(e.getKiller() instanceof Player)) return;

        Player player = (Player) e.getKiller();
        UUID uuid = player.getUniqueId();
        MythicMob mob = e.getMobType();

        String id = mob.getInternalName();
        if (!requirement.containsKey(id)) return;

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        progress.push(id);
        progress.update();
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    @Getter
    public static class MobKillerProgress extends AbstractProgress {

        private final Map<String, Integer> kill;

        public MobKillerProgress(UUID uuid, String questID, Map<String, Integer> kill) {
            super(uuid, questID);
            this.kill = kill;
        }

        public MobKillerProgress(UUID uuid, String questID, String data) {
            super(uuid, questID);
            this.kill = JSON.parseObject(data, new TypeReference<LinkedHashMap<String, Integer>>() {});
        }

        private int getCount(String key) {
            return this.kill.get(key);
        }

        @Override
        public void push(String key) {
            this.kill.computeIfPresent(key, (k, v) -> Math.max(0, v - 1));
        }

        @Override
        public void push(String key, int i) {
            this.kill.computeIfPresent(key, (k, v) -> Math.max(0, v - i));
        }

        @Override
        public boolean isFinished() {
            for (int count : this.kill.values()) {
                if (count > 0) return false;
            }

            return true;
        }

        @Override
        public String getConvertData() {
            return JSON.toJSONString(kill);
        }
    }
}
