package net.sakuragame.eternal.justquest.util;

import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import ink.ptms.zaphkiel.ZaphkielAPI;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.core.data.PageResult;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static void giveItems(Player player, Map<String, Integer> items) {
        int i = 1;
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(entry.getKey(), player);
            if (item != null) {
                item.setAmount(entry.getValue());
                player.getInventory().addItem(item);
                PacketSender.putClientSlotItem(player, "quest_items_" + i, item);
            }
            i++;
        }

        PacketSender.sendRunFunction(player, "default", "global.quest_items_count = " + items.size() + ";", true);
        PacketSender.sendOpenHud(player, "quest_items");
    }

    public static void sendNotify(Player player, String title, String contents) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("quest_notice_title", title);
        placeholder.put("quest_notice_contents", contents);

        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendRunFunction(player, "default", "global.quest_notice_visible = 1;", true);
        PacketSender.sendOpenHud(player, "quest_notice");
    }

    public static void setTraceBar(Player player, String title) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        placeholder.put("trace_desc", "");
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, new YamlConfiguration());

        Scheduler.runLaterAsync(() -> PacketSender.sendOpenHud(player, "quest_trace"), 1);
    }

    public static void setTraceBar(Player player, String title, List<String> desc, ScreenUI contents) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        placeholder.put("trace_desc", String.join("\n", desc));
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, contents.build(null));

        Scheduler.runLaterAsync(() -> PacketSender.sendOpenHud(player, "quest_trace"), 1);
    }

    public static PageResult getPagePart(List<String> list, int page) {
        int size = list.size();
        int total = size / 6;

        int current = Math.min(page, total);
        int from = current * 6;
        int to = (page > total) ? size : Math.min((page + 1) * 6, size);

        return new PageResult(current + 1, total, new ArrayList<>(list.subList(from, to)));
    }
}
