package net.sakuragame.eternal.justquest.core.mission.sub;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.SlotComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import ink.ptms.zaphkiel.ZaphkielAPI;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.mission.progress.CountProgress;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.juststore.api.event.StoreOpenEvent;
import net.sakuragame.eternal.juststore.api.event.StoreTradeEvent;
import net.sakuragame.eternal.juststore.core.store.StoreType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class StoreTradeMission extends AbstractMission {

    private final String id;
    private final int count;
    private final int type;

    public StoreTradeMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        super(ID, type, navigationEvents, completeEvents, descriptions, section);
        this.id = section.getString("id");
        this.count = section.getInt("count");
        this.type = section.getInt("type");
    }

    @Override
    public String getPlugin() {
        return "JustStore";
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(id, null);
        if (item == null) return null;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        CountProgress progress = (CountProgress) this.getData(uuid);
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new SlotComp("s_" + id, "quest_item_" + id)
                        .setDrawBackground(false)
                        .setXY("objectives.x", "objectives.y")
                        .setCompSize(10, 10)
                        .setExtend("objectives")
                )
                .addComponent(new LabelComp("l_" + id, name + ": " + (this.count - progress.getCount()) + "/" + this.count)
                        .setXY("s_" + id + ".x+12", "s_" + id + ".y+0.5")
                        .setExtend("objectives")
                );
        PacketSender.putClientSlotItem(player, "quest_item_" + id, item);

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(id, null);
        if (item == null) return null;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);
        ui
                .addComponent(new SlotComp("s_" + id, "quest_item_" + id)
                        .setDrawBackground(false)
                        .setXY("objectives.x", "objectives.y")
                        .setCompSize(10, 10)
                        .setExtend("objectives")
                )
                .addComponent(new LabelComp("l_" + id, name + ": " + this.count + "/" + this.count)
                        .setXY("s_" + id + ".x+12", "s_" + id + ".y+0.5")
                        .setExtend("objectives")
                );
        PacketSender.putClientSlotItem(player, "quest_item_" + id, item);

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
    public void onTrade(StoreTradeEvent.Post e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        String commodityID = e.getCommodity().getId();
        int amount = e.getQuantity();

        if (!this.id.equals(commodityID)) return;

        progress.push(amount);
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(StoreOpenEvent e) {
        if (e.isCancelled()) return;
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        IProgress progress = this.getData(uuid);
        if (progress == null) return;

        e.setType(StoreType.match(this.type));
    }
}
