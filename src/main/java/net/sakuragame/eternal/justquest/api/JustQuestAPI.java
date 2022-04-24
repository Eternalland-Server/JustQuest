package net.sakuragame.eternal.justquest.api;

import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;

public class JustQuestAPI {

    public static void allotQuest(Player player, String id) {
        JustQuest.getQuestManager().allotQuest(player.getUniqueId(), id);
    }
}
