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
    private String trace;
    private final List<String> finished;
    private final Map<String, QuestProgress> questProgress;

    public QuestAccount(UUID uuid, String trace, List<String> finished, Map<String, QuestProgress> questProgress) {
        this.uuid = uuid;
        this.trace = trace;
        this.finished = finished;
        this.questProgress = questProgress;
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
                JustQuest.getQuestManager().saveQuest(
                        this.uuid,
                        k.getMissionID()
                );
            }
        });
    }

    public List<String> getQuests() {
        return this.questProgress
                .keySet().stream()
                .sorted(Comparator.comparingInt(key -> JustQuest.getProfileManager().getQuest(key).getType().getID()))
                .collect(Collectors.toList());
    }

    public void setTrace(String trace) {
        if (trace == null) {
            if (this.questProgress.size() == 0) this.trace = null;
            else this.trace = this.questProgress.keySet().stream().findFirst().get();
        }
        else {
            if (!this.questProgress.containsKey(trace)) return;
            this.trace = trace;
        }

        System.out.println("set trace: " + this.trace);
        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateTrace(this.uuid, trace));
    }

    public void updateTraceBar() {
        Player player = Bukkit.getPlayer(this.uuid);
        player.sendMessage("update trace ui");

        if (this.trace == null) {
            Utils.setTraceBar(player, "&7&o暂未跟踪任何任务&7&l(F)");
            return;
        }

        String title = JustQuest.getProfileManager().getQuest(this.trace).getName();
        QuestProgress progress = this.questProgress.get(this.trace);

        Utils.setTraceBar(player, title,
                progress.isCompleted() ?
                        JustQuest.getProfileManager()
                                .getMission(progress.getMissionID())
                                .getCompleteDisplay() :
                        JustQuest.getProfileManager()
                                .getMission(progress.getMissionID())
                                .getProgressDisplay(uuid)
        );
    }

    public void saveProgress(String questID, String missionID, String data) {
        QuestProgress progress = this.questProgress.computeIfAbsent(questID, k -> new QuestProgress(questID, missionID, data));
        progress.setMissionID(missionID);
        progress.setData(data);

        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateQuestProgress(this.uuid, this.questProgress.get(questID)));
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

    public void completeQuest(String questID) {
        QuestProgress data = this.questProgress.get(questID);
        data.setState(QuestState.Completed);
        if (questID.equals(this.trace)) this.updateTraceBar();

        Scheduler.runAsync(() -> JustQuest.getStorageManager().updateQuestProgress(this.uuid, data));
    }

    public void deleteQuest(String questID) {
        this.questProgress.remove(questID);
        if (questID.equals(this.trace)) {
            this.setTrace(null);
            this.updateTraceBar();
        }

        Scheduler.runAsync(() -> JustQuest.getStorageManager().deleteQuestProgress(this.uuid, questID));
    }
}
