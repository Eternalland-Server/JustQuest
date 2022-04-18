package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;

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

    public void saveQuest(UUID uuid, String id) {
        IMission mission = JustQuest.getProfileManager().getMission(id);
        if (mission == null) return;

        mission.restrain(uuid);
    }
}
