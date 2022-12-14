package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.condition.ICondition;
import net.sakuragame.eternal.justquest.core.event.IEvent;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class QuestManager {

    public void allotQuest(UUID uuid, String questID) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        if (quest == null) return;

        quest.allot(uuid);
    }

    public void resumeQuest(UUID uuid, String questID, String missionID) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        if (quest == null) return;

        quest.resume(uuid, missionID);
    }

    public void fireEvents(UUID uuid, List<String> events) {
        this.fireEvents(Bukkit.getPlayer(uuid), events);
    }

    public void fireEvents(Player player, List<String> events) {
        events.forEach(key -> {
            IEvent event = JustQuest.getProfileManager().getEvent(key);
            if (event != null) {
                event.execute(player);
            }
        });
    }

    public boolean meetConditions(Player player, List<String> conditions) {
        for (String key : conditions) {
            ICondition condition = JustQuest.getProfileManager().getCondition(key);
            if (condition.meet(player)) continue;
            return false;
        }
        return true;
    }
}
