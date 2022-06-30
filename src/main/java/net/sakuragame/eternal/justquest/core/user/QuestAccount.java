package net.sakuragame.eternal.justquest.core.user;

import lombok.Getter;
import lombok.Setter;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.QuestEvent;
import net.sakuragame.eternal.justquest.core.data.QuestState;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.util.Scheduler;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class QuestAccount {

    private final UUID uuid;
    private String trace;
    private int chain;
    private List<String> finished;
    private Map<String, QuestProgress> progresses;

    public QuestAccount(UUID uuid) {
        this.uuid = uuid;
        this.trace = null;
        this.chain = 0;
    }

    public QuestAccount(UUID uuid, String trace, int chain, List<String> finished, Map<String, QuestProgress> progresses) {
        this.uuid = uuid;
        this.trace = trace;
        this.chain = chain;
        this.finished = finished;
        this.progresses = progresses;
    }

    public void purgeData() {
        this.trace = null;
        this.chain = 0;
        this.finished.clear();
        this.progresses.values().forEach(k -> {
            IMission mission = JustQuest.getProfileManager().getMission(k.getMissionID());
            if (mission != null) {
                mission.abandon(uuid);
            }
        });
        this.progresses.clear();
        this.updateTraceBar();

        Scheduler.runAsync(() -> JustQuest.getStorageManager().purgeUserData(this.uuid));
    }

    public List<String> getQuests() {
        List<String> result = new ArrayList<>();
        this.progresses
                .values().stream()
                .sorted()
                .collect(Collectors.toList())
                .forEach(k -> result.add(k.getQuestID()));

        return result;
    }

    public void resumeQuestsProgress() {
        progresses.values().forEach(k -> JustQuest.getQuestManager().resumeQuest(
                this.uuid,
                k.getQuestID(),
                k.getMissionID()
        ));
    }

    public void saveQuestsProgress() {
        progresses.values().forEach(k -> {
            if (!k.isCompleted()) {
                IMission mission = JustQuest.getProfileManager().getMission(k.getMissionID());
                mission.restrain(this.uuid);

                JustQuest.getStorageManager().updateQuestProgress(uuid, k);
            }
        });
    }

    public void newQuestProgress(String questID, String missionID, IProgress progress) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);

        QuestProgress questProgress = new QuestProgress(questID, missionID, progress, quest.getExpireTime());
        this.progresses.put(questID, questProgress);

        Scheduler.runAsync(() -> JustQuest.getStorageManager().insertQuestProgress(this.uuid, questProgress));
    }

    public QuestState getQuestState(String questID) {
        if (this.finished.contains(questID)) return QuestState.Finished;
        if (this.progresses.containsKey(questID)) {
            return this.progresses.get(questID).getState();
        }
        return QuestState.Pending;
    }

    public void addFinished(String questID) {
        if (this.finished.contains(questID)) return;
        this.finished.add(questID);
        Scheduler.runAsync(() -> JustQuest.getStorageManager().insertFinished(uuid, questID));
    }

    public void sendCompletedCount() {
        int i = 0;
        for (QuestProgress progress : this.progresses.values()) {
            if (!progress.isCompleted()) continue;
            i++;
        }

        PacketSender.sendRunFunction(Bukkit.getPlayer(this.uuid), "default", "global.quest_completed_count = " + i + ";", false);
    }

    public void updateChain(int chain) {
        this.chain = chain;
        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateChain(this.uuid, this.chain));
    }

    public void setQuestTrace(String id) {
        if (id == null) {
            this.updateTraceBar();
            return;
        }

        if (!this.progresses.containsKey(id)) return;
        if (this.trace != null && this.trace.equals(id)) return;

        this.trace = id;

        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateTrace(this.uuid, this.trace));
    }

    public IMission getTraceMission() {
        if (this.trace == null) return null;
        QuestProgress progress = this.progresses.get(this.trace);
        if (progress == null) return null;
        return JustQuest.getProfileManager().getMission(progress.getMissionID());
    }

    public void updateTraceBar() {
        Player player = Bukkit.getPlayer(this.uuid);

        if (!this.progresses.containsKey(this.trace)) {
            this.trace = this.progresses.size() != 0 ? this.progresses.keySet().stream().findFirst().get() : null;
            Scheduler.runAsync(() -> JustQuest.getStorageManager().updateTrace(this.uuid, this.trace));
        }

        if (this.trace == null) {
            Utils.setTraceBar(player, "&7&o暂未跟踪任何任务");
            return;
        }

        IQuest quest = JustQuest.getProfileManager().getQuest(this.trace);
        QuestProgress progress = this.progresses.get(this.trace);

        String title = quest.getTitleDisplay(this.uuid) + (progress.isCompleted() ? " ❋" : "");
        List<String> desc = JustQuest.getProfileManager().getMission(progress.getMissionID()).getDescriptions(uuid);

        Utils.setTraceBar(player, title, desc,
                progress.isCompleted() ?
                        JustQuest.getProfileManager()
                                .getMission(progress.getMissionID())
                                .getCompleteDisplay(uuid) :
                        JustQuest.getProfileManager()
                                .getMission(progress.getMissionID())
                                .getProgressDisplay(uuid)
        );
    }

    public void completeQuest(String questID) {
        QuestProgress data = this.progresses.get(questID);
        data.setState(QuestState.Completed);
        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateQuestProgress(this.uuid, data));

        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        quest.completed(this.uuid);
        QuestEvent.Completed event = new QuestEvent.Completed(Bukkit.getPlayer(this.uuid), quest);
        event.call();

        String nextID = quest.getNextQuest();
        if (nextID != null) {
            IQuest next = JustQuest.getProfileManager().getQuest(nextID);
            if (next != null) next.allot(this.uuid);
            if (questID.equals(this.trace)) this.setQuestTrace(nextID);
        }

        this.updateTraceBar();
    }

    public void cancelQuest(String questID) {
        QuestProgress data = this.progresses.remove(questID);
        IMission mission = JustQuest.getProfileManager().getMission(data.getMissionID());
        mission.abandon(this.uuid);

        if (this.trace.equals(questID)) {
            this.updateTraceBar();
        }
    }

    public void completeMission(String questID, String missionID) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        IMission next = quest.getNextMission(missionID);
        if (next == null) {
            this.completeQuest(questID);
            return;
        }

        IMission mission = JustQuest.getProfileManager().getMission(missionID);
        mission.abandon(uuid);
        next.active(uuid, questID);
    }

    public void deleteQuestProgress(String questID) {
        this.progresses.remove(questID);

        Scheduler.runAsync(() -> JustQuest.getStorageManager().deleteQuestProgress(this.uuid, questID));
    }
}
