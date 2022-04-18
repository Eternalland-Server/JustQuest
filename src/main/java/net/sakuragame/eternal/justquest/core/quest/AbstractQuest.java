package net.sakuragame.eternal.justquest.core.quest;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class AbstractQuest implements IQuest {

    private final String ID;
    private final String name;

    private final List<String> missions;

    private final QuestReward reward;

    public AbstractQuest(String ID, String name, List<String> missions, QuestReward reward) {
        this.ID = ID;
        this.name = name;
        this.missions = missions;
        this.reward = reward;
    }

    @Override
    public void allot(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        if (account.getQuestProgress().containsKey(this.getID())) return;

        IMission mission = JustQuest.getProfileManager().getMission(this.getMissions().get(0));
        if (mission == null) return;

        mission.active(uuid, this.getID());
    }

    @Override
    public void resume(UUID uuid, String missionID, String data) {
        if (!this.missions.contains(missionID)) return;

        IMission mission = JustQuest.getProfileManager().getMission(missionID);
        if (mission == null) return;

        mission.keep(uuid, this.ID, data);
    }

    @Override
    public void cancel(UUID uuid) {
        if (!this.isAllowCancel()) return;

        // TODO
    }

    @Override
    public String nextMission(String id) {
        int index = this.missions.indexOf(id);
        if (index == -1) return null;
        if (index + 1 >= this.missions.size()) return null;
        return this.missions.get(index + 1);
    }
}
