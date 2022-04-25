package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.event.IEvent;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
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

    public void resumeQuest(UUID uuid, String questID, String missionID, String data) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        if (quest == null) return;

        quest.resume(uuid, missionID, data);
    }

    public void saveProgress(UUID uuid, String id) {
        IMission mission = JustQuest.getProfileManager().getMission(id);
        if (mission == null) return;

        mission.restrain(uuid);
    }

    public void fireEvents(Player player, List<String> events) {
        events.forEach(key -> {
            IEvent event = JustQuest.getProfileManager().getEvent(key);
            if (event != null) {
                event.execute(player);
            }
        });
    }

    public void fireEvents(UUID uuid, List<String> events) {
        this.fireEvents(Bukkit.getPlayer(uuid), events);
    }
}
