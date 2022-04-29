package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.SlotComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.ExpendProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CollectMission extends AbstractMission {

    private final Map<String, Integer> requirement;
    private final boolean remove;

    public CollectMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.requirement = new LinkedHashMap<>();
        this.remove = section.getBoolean("remove", false);

        for (String key : section.getKeys(false)) {
            int amount = section.getInt(key);
            this.requirement.put(key, amount);
        }
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ExpendProgress progress = (ExpendProgress) this.getData(uuid);

        int i = 0;
        for (Map.Entry<String, Integer> entry : this.requirement.entrySet()) {
            String id = entry.getKey();
            int amount = entry.getValue();

            ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(id, null);
            if (item == null) continue;
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            ui
                    .addComponent(new SlotComp("s_" + i, "quest_collect_" + id)
                            .setDrawBackground(false)
                            .setXY("objectives.x", "objectives.y+" + i * 12)
                            .setCompSize(10, 10)
                            .setExtend("objectives")
                    )
                    .addComponent(new LabelComp("l_" + i, name + ": " + (amount - progress.getCount(id)) + "/" + amount)
                            .setXY("s_" + i + ".x+12", "s_" + i + ".y+0.5")
                            .setExtend("objectives")
                    );
            i++;

            PacketSender.putClientSlotItem(player, "quest_collect_" + id, item);
        }

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);

        int i = 0;
        for (Map.Entry<String, Integer> entry : this.requirement.entrySet()) {
            String id = entry.getKey();
            int amount = entry.getValue();

            ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(id, null);
            if (item == null) continue;
            String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

            ui
                    .addComponent(new SlotComp("s_" + i, "quest_collect_" + id)
                            .setDrawBackground(false)
                            .setXY("objectives.x", "objectives.y+" + i * 12)
                            .setCompSize(10, 10)
                            .setExtend("objectives")
                    )
                    .addComponent(new LabelComp("l_" + i, name + ": " + amount + "/" + amount)
                            .setXY("s_" + i + ".x+12", "s_" + i + ".y+0.5")
                            .setExtend("objectives")
                    );
            i++;

            PacketSender.putClientSlotItem(player, "quest_collect_" + id, item);
        }

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new ExpendProgress(uuid, questID, new LinkedHashMap<>(this.requirement));
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new ExpendProgress(uuid, questID, data);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        UUID uuid = player.getUniqueId();
        ItemStack itemStack = e.getItem().getItemStack();
        Item item = ZaphkielAPI.INSTANCE.getItem(itemStack);
        if (item == null) return;

        String id = item.getId();
        if (!this.requirement.containsKey(id)) return;

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        if (this.remove) {
            e.getItem().remove();
            e.setCancelled(true);
        }

        progress.push(id, itemStack.getAmount());
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }
}
