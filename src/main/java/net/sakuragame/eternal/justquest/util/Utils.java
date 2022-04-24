package net.sakuragame.eternal.justquest.util;

import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.core.data.PageResult;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    public static void setTraceBar(Player player, String title) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        placeholder.put("trace_desc", "");
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, new YamlConfiguration());
        PacketSender.sendRunFunction(
                player,
                "default",
                "func.delay(50);func.Screen_Open_Hud('quest_trace');",
                true
        );
    }

    public static void setTraceBar(Player player, String title, List<String> desc, ScreenUI contents) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        placeholder.put("trace_desc", String.join("\n", desc));
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, contents.build(null));
        PacketSender.sendRunFunction(
                player,
                "default",
                "func.delay(50);func.Screen_Open_Hud('quest_trace');",
                true
        );
    }

    public static PageResult getPagePart(List<String> list, int page) {
        int size = list.size();
        int total = size / 6;

        int current = Math.min(page, total);
        int from = current * 6;
        int to = (page > total) ? size : Math.min((page + 1) * 6, size);

        return new PageResult(current, to, new ArrayList<>(list.subList(from, to)));
    }
}
