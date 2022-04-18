package net.sakuragame.eternal.justquest.util;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
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

    private static final YamlConfiguration done;

    static {
        ScreenUI ui = new ScreenUI(QuestUIManager.QUEST_OBJECTIVE_ID)
                .addComponent(new LabelComp("done", "&a&l奖励可领取")
                        .setExtend("objectives")
                );

        done = ui.build(null);
    }

    public static void setTrace(Player player, String title) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, new YamlConfiguration());
        Scheduler.runLaterAsync(() -> PacketSender.sendOpenHud(player, "traceBar"), 1);
    }

    public static void setTrace(Player player, String title, ScreenUI contents) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, contents.build(null));
        Scheduler.runLaterAsync(() -> PacketSender.sendOpenHud(player, "traceBar"), 1);
    }

    public static void setTraceDone(Player player, String title) {
        Map<String, String> placeholder = new HashMap<>();
        placeholder.put("trace_title", title);
        PacketSender.sendSyncPlaceholder(player, placeholder);
        PacketSender.sendYaml(player, FolderType.Gui, QuestUIManager.QUEST_OBJECTIVE_ID, done);
        Scheduler.runLaterAsync(() -> PacketSender.sendOpenHud(player, "traceBar"), 1);
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
