package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.ExpendProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ConsumeMission extends AbstractMission {

    private final Map<String, Integer> requirement;

    public ConsumeMission(String ID, String type, List<String> events, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, events, descriptions, section);
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
            Item item = ZaphkielAPI.INSTANCE.getRegisteredItem().get(k);
            if (item != null) {
                String name = ChatColor.stripColor(item.getName().get("NAME"));
                display.add("⊑&f" + name + ": " + (v - progress.getCount(k)) + "/" + v);
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
    public ScreenUI getCompleteDisplay(UUID uuid) {
        List<String> display = new ArrayList<>();
        requirement.forEach((k, v) -> {
            Item item = ZaphkielAPI.INSTANCE.getRegisteredItem().get(k);
            if (item != null) {
                String name = ChatColor.stripColor(item.getName().get("NAME"));
                display.add("⊑&f" + name + ": " + v + "/" + v);
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
        return new ExpendProgress(uuid, questID, this.requirement);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new ExpendProgress(uuid, questID, data);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        ItemStack itemStack = e.getItem();
        Item item = ZaphkielAPI.INSTANCE.getItem(itemStack);
        if (item == null) return;

        if (!this.requirement.containsKey(item.getId())) return;

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        progress.push(item.getId());
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }
}
