package net.sakuragame.eternal.justquest.core.user;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.data.QuestState;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.util.Scheduler;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class QuestAccount {

    private final UUID uuid;
    private String questTrace;
    private final List<String> finished;
    private final Map<String, QuestProgress> questProgress;

    public QuestAccount(UUID uuid, String questTrace, List<String> finished, Map<String, QuestProgress> questProgress) {
        this.uuid = uuid;
        this.questTrace = questTrace;
        this.finished = finished;
        this.questProgress = questProgress;
    }

    public IQuest getTraceQuest() {
        if (this.questTrace == null) return null;
        return JustQuest.getProfileManager().getQuest(this.questTrace);
    }

    public IMission getTraceMission() {
        if (this.questTrace == null) return null;
        QuestProgress progress = this.questProgress.get(this.questTrace);
        if (progress == null) return null;
        return JustQuest.getProfileManager().getMission(progress.getMissionID());
    }

    public void purgeData() {
        this.questTrace = null;
        this.finished.clear();
        this.questProgress.keySet().forEach(k -> {
            IMission mission = JustQuest.getProfileManager().getMission(k);
            if (mission != null) {
                mission.abandon(uuid);
            }
        });
        this.questProgress.clear();
        this.updateTraceBar();

        Scheduler.runAsync(() -> JustQuest.getStorageManager().purgeUserData(this.uuid));
    }

    public List<String> getQuests() {
        return this.questProgress
                .keySet().stream()
                .sorted(Comparator.comparingInt(key -> JustQuest.getProfileManager().getType(key).getID()))
                .collect(Collectors.toList());
    }

    public void resumeQuestsProgress() {
        questProgress.values().forEach(k -> {
            if (!k.isCompleted()) {
                JustQuest.getQuestManager().resumeQuest(
                        this.uuid,
                        k.getQuestID(),
                        k.getMissionID(),
                        k.getData()
                );
            }
        });
    }

    public void saveQuestsProgress() {
        questProgress.values().forEach(k -> {
            if (!k.isCompleted()) {
                JustQuest.getQuestManager().saveProgress(
                        this.uuid,
                        k.getMissionID()
                );
            }
        });
    }

    public void saveQuestProgress(String questID, String missionID, String data) {
        QuestProgress progress = this.questProgress.computeIfAbsent(questID, k -> new QuestProgress(questID, missionID, data));
        progress.setMissionID(missionID);
        progress.setData(data);

        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateQuestProgress(this.uuid, this.questProgress.get(questID)));
    }

    public void setQuestTrace(String id) {
        if (id == null) {
            this.updateTraceBar();
            return;
        }

        if (!this.questProgress.containsKey(id)) {
            return;
        }

        this.questTrace = id;
        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateTrace(this.uuid, this.questTrace));
    }

    public void autoQuestTrace() {
        if (this.questProgress.size() != 0) {
            this.questTrace = this.questProgress.keySet().stream().findFirst().get();
        }
        else {
            this.questTrace = null;
        }

        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateTrace(this.uuid, this.questTrace));
    }

    public void updateTraceBar() {
        Player player = Bukkit.getPlayer(this.uuid);

        if (this.questTrace == null) {
            Utils.setTraceBar(player, "&7&o暂未跟踪任何任务&7&l(F)");
            return;
        }

        IQuest quest = JustQuest.getProfileManager().getQuest(this.questTrace);
        QuestProgress progress = this.questProgress.get(this.questTrace);

        String title = quest.getType().getSymbol() + " " + quest.getName() + (progress.isCompleted() ? " ❋" : "");
        List<String> desc = JustQuest.getProfileManager().getMission(progress.getMissionID()).getDescriptions();

        Utils.setTraceBar(player, title, desc,
                progress.isCompleted() ?
                        JustQuest.getProfileManager()
                                .getMission(progress.getMissionID())
                                .getCompleteDisplay() :
                        JustQuest.getProfileManager()
                                .getMission(progress.getMissionID())
                                .getProgressDisplay(uuid)
        );
    }

    public void completeQuest(String questID) {
        QuestProgress data = this.questProgress.get(questID);
        data.setState(QuestState.Completed);

        if (questID.equals(this.questTrace)) {
            String nextID = JustQuest.getProfileManager().getQuest(questID).getNext();
            if (nextID != null) {
                IQuest next = JustQuest.getProfileManager().getQuest(nextID);
                if (next == null) {
                    this.updateTraceBar();
                    return;
                }
                next.allot(this.uuid);
                this.setQuestTrace(nextID);
                this.updateTraceBar();
            }
            else {
                this.updateTraceBar();
            }
        }

        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateQuestProgress(this.uuid, data));
    }

    public void cancelQuest(String questID) {
        QuestProgress data = this.questProgress.remove(questID);
        IMission mission = JustQuest.getProfileManager().getMission(data.getMissionID());
        mission.abandon(this.uuid);

        if (this.questTrace.equals(questID)) {
            this.autoQuestTrace();
            this.updateTraceBar();
        }
    }

    public void completeMission(String questID, String missionID) {
        IQuest quest = JustQuest.getProfileManager().getQuest(questID);
        String next = quest.nextMission(missionID);
        if (next == null) {
            this.completeQuest(questID);
            return;
        }

        IMission mission = JustQuest.getProfileManager().getMission(next);
        mission.active(uuid, questID);
    }

    public void deleteProgress(String questID) {
        this.questProgress.remove(questID);
        if (questID.equals(this.questTrace)) {
            this.autoQuestTrace();
            this.updateTraceBar();
        }

        Scheduler.runAsync(() -> JustQuest.getStorageManager().deleteQuestProgress(this.uuid, questID));
    }
}
