package net.sakuragame.eternal.justquest.core.mission.sub;

import com.google.gson.JsonObject;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.SlotComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import ink.ptms.zaphkiel.ZaphkielAPI;
import ink.ptms.zaphkiel.api.Item;
import ink.ptms.zaphkiel.api.ItemStream;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import lombok.Getter;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ChainQuestSubmitEvent;
import net.sakuragame.eternal.justquest.core.chain.ChainRequire;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChainMission extends AbstractMission {

    private final static DecimalFormat FORMAT = new DecimalFormat("0.00");

    public ChainMission(String ID, String type, List<String> descriptions) {
        super(ID, type, new ArrayList<>(), new ArrayList<>(), descriptions, null);
    }

    @Override
    public List<String> getDescriptions(UUID uuid) {
        ChainProgress progress = (ChainProgress) this.getData(uuid);
        ChainRequire require = JustQuest.getChainManager().getRequire(progress.getId());

        List<String> desc = new ArrayList<>();
        for (String s : this.getDescriptions()) {
            desc.add(s
                    .replace("<dungeon>", require.getDungeon())
                    .replace("<mobs>", require.getMobs())
            );
        }

        return desc;
    }

    @Override
    public ScreenUI getProgressDisplay(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        ChainProgress progress = (ChainProgress) this.getData(uuid);
        ChainRequire require = JustQuest.getChainManager().getRequire(progress.getId());

        ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(require.getItem(), null);
        if (item == null) return null;
        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);

        ui
                .addComponent(new SlotComp("s_i", "quest_collect_chain")
                        .setDrawBackground(false)
                        .setXY("objectives.x", "objectives.y")
                        .setCompSize(10, 10)
                        .setExtend("objectives")
                )
                .addComponent(new LabelComp("l_i", name + ": " + (progress.getTotal() - progress.getCurrent()) + "/" + progress.getTotal())
                        .setXY("s_i.x+12", "s_i.y+0.5")
                        .setExtend("objectives")
                );
        PacketSender.putClientSlotItem(player, "quest_collect_chain", item);

        return ui;
    }

    @Override
    public ScreenUI getCompleteDisplay(UUID uuid) {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID);

        ui
                .addComponent(new LabelComp("finished", "⁍跑环任务已完成")
                        .setXY("objectives.x", "objectives.y")
                        .setExtend("objectives")
                );

        return ui;
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID) {
        return new ChainProgress(uuid, questID);
    }

    @Override
    public IProgress newProgress(UUID uuid, String questID, String data) {
        return new ChainProgress(uuid, questID, data);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();
        UUID uuid = player.getUniqueId();
        ItemStack itemStack = e.getItem().getItemStack();
        ItemStream itemStream = ZaphkielAPI.INSTANCE.read(itemStack);
        if (itemStream.isVanilla()) return;

        ChainProgress progress = (ChainProgress) this.getData(uuid);
        if (progress == null || progress.isFinished()) return;

        String id = itemStream.getZaphkielName();
        ChainRequire require = JustQuest.getChainManager().getRequire(progress.getId());
        if (!require.getItem().equals(id)) return;

        e.getItem().remove();
        e.setCancelled(true);

        progress.push(itemStack.getAmount());
        if (!progress.isFinished()) {
            progress.update();
            return;
        }

        this.complete(uuid);
    }

    @EventHandler
    public void onSubmit(ChainQuestSubmitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        ChainProgress progress = (ChainProgress) this.getData(uuid);
        if (progress == null || progress.isFinished()) return;

        ChainRequire require = JustQuest.getChainManager().getRequire(progress.getId());

        int count = 0;

        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (MegumiUtil.isEmpty(item)) continue;

            ItemStream itemStream = ZaphkielAPI.INSTANCE.read(item);
            if (itemStream.isVanilla()) continue;

            String id = itemStream.getZaphkielName();
            if (!require.getItem().equals(id)) continue;

            int amount = item.getAmount();
            int current = progress.getCurrent();

            int result = current - amount;
            if (result >= 0) {
                player.getInventory().setItem(i, new ItemStack(Material.AIR));
                progress.push(amount);
                count += amount;
                if (!progress.isFinished()) continue;
                this.complete(uuid);
                return;
            }
            else {
                item.setAmount(Math.abs(result));
                player.getInventory().setItem(i, item);
                progress.push(current);
                this.complete(uuid);
                return;
            }
        }

        if (count == 0) {
            player.sendMessage(ConfigFile.prefix + "你背包内没有相关材料可交付");
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.4f, 1);
            return;
        }

        progress.update();
        player.sendTitle("", "§3§l跑环交付进度 §7§l- §8§l[§e§l" + FORMAT.format(progress.getRatio() * 100) + "%§8§l]", 0, 60, 0);
    }

    @Getter
    private static class ChainProgress extends AbstractProgress {

        private final String id;
        private int current;
        private final int total;

        public ChainProgress(UUID uuid, String questID) {
            super(uuid, questID);
            String cache = JustQuest.getChainManager().takeCache(uuid);
            ChainRequire require = JustQuest.getChainManager().getRequire(cache);
            this.id = cache;
            this.total = require.getRandomAmount();
            this.current = total;
        }

        public ChainProgress(UUID uuid, String questID, String data) {
            super(uuid, questID);
            JsonObject object = Utils.parse(data);
            this.id = object.get("id").getAsString();
            this.current = object.get("current").getAsInt();
            this.total = object.get("total").getAsInt();
        }

        @Override
        public void push() {}

        @Override
        public void push(String key) {}

        @Override
        public void push(int i) {
            this.current = Math.max(0, this.current - i);
        }

        @Override
        public void push(String key, int i) {}

        @Override
        public boolean isFinished() {
            return this.current <= 0;
        }

        public double getRatio() {
            return (this.total - this.current) / (double) this.total;
        }

        @Override
        public String getConvertData() {
            JsonObject object = new JsonObject();
            object.addProperty("id", this.id);
            object.addProperty("current", this.current);
            object.addProperty("total", this.total);

            return object.toString();
        }
    }
}
