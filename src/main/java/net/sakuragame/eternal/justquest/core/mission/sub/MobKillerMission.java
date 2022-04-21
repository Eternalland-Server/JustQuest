package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.ExpendProgress;
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
        ExpendProgress progress = (ExpendProgress) this.getData(uuid);
        requirement.forEach((k, v) -> {
            MythicMob mob = MythicMobs.inst().getAPIHelper().getMythicMob(k);
            if (mob != null) {
                String name = ChatColor.stripColor(mob.getDisplayName().get());
                display.add("&f" + name + ": " + (v - progress.getCount(k)) + "/" + v);
            }
            else {
                display.add(k + ": error");
            }
        });

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", String.join("\n", display))
                        .setExtend("objectives")
                );

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay() {
        List<String> display = new ArrayList<>();
        requirement.forEach((k, v) -> {
            MythicMob mob = MythicMobs.inst().getAPIHelper().getMythicMob(k);
            if (mob != null) {
                String name = ChatColor.stripColor(mob.getDisplayName().get());
                display.add("&f" + name + ": " + v + "/" + v);
            }
            else {
                display.add(k + ": error");
            }
        });

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new LabelComp("require", String.join("\n", display))
                        .setExtend("objectives")
                );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new ExpendProgress(uuid, questID, new LinkedHashMap<>(requirement));
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new ExpendProgress(uuid, questID, data);
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
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }
}
