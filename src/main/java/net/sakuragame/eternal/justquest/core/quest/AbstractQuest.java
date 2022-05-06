package net.sakuragame.eternal.justquest.core.quest;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.QuestEvent;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class AbstractQuest implements IQuest {

    private final String ID;
    private final String name;
    private final List<String> descriptions;

    private final List<String> missions;

    private final String next;

    private final QuestReward reward;

    public AbstractQuest(String ID, String name, List<String> descriptions, List<String> missions, String next, QuestReward reward) {
        this.ID = ID;
        this.name = name;
        this.descriptions = descriptions;
        this.missions = missions;
        this.next = next;
        this.reward = reward;
    }

    @Override
    public void allot(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        if (account.getFinished().contains(this.getID())) return;
        if (account.getProgresses().containsKey(this.getID())) return;

        String missionID = this.getMissions().get(0);
        IMission mission = JustQuest.getProfileManager().getMission(missionID);
        if (mission == null) return;

        mission.active(uuid, this.getID());

        QuestEvent.Allot event = new QuestEvent.Allot(Bukkit.getPlayer(uuid), this);
        event.call();
    }

    @Override
    public void resume(UUID uuid, String missionID) {
        if (!this.missions.contains(missionID)) return;

        IMission mission = JustQuest.getProfileManager().getMission(missionID);
        if (mission == null) return;

        mission.keep(uuid, this.ID);
    }

    @Override
    public void cancel(UUID uuid) {
        if (!this.isAllowCancel()) return;

        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        account.cancelQuest(this.ID);

        QuestEvent.Cancel event = new QuestEvent.Cancel(Bukkit.getPlayer(uuid), this);
        event.call();
    }

    @Override
    public void award(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        QuestProgress progress = account.getProgresses().get(this.ID);
        if (progress == null) return;
        if (!progress.isCompleted()) return;

        IMission mission = JustQuest.getProfileManager().getMission(progress.getMissionID());
        mission.restrain(uuid);

        this.reward.apply(uuid);

        Scheduler.runAsync(() -> {
            account.deleteQuestProgress(this.ID);
            if (this.getType().isOnce()){
                account.addFinished(this.ID);
            }
        });

        QuestEvent.Finished event = new QuestEvent.Finished(Bukkit.getPlayer(uuid), this);
        event.call();
    }

    @Override
    public String getNextQuest() {
        return this.next;
    }

    @Override
    public IMission getNextMission(String id) {
        int index = this.missions.indexOf(id);
        if (index == -1) return null;
        if (index + 1 >= this.missions.size()) return null;
        return JustQuest.getProfileManager().getMission(this.missions.get(index + 1));
    }

    @Override
    public String getRewardDesc(UUID uuid) {
        return this.reward.getRewardDescriptions();
    }

    @Override
    public Map<String, Integer> getRewardItems(UUID uuid) {
        return this.reward.getItems();
    }

    @Override
    public long getExpireTime() {
        return -1;
    }
}
