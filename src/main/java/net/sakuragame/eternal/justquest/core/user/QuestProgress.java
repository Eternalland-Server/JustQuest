package net.sakuragame.eternal.justquest.core.user;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.data.QuestState;
import net.sakuragame.eternal.justquest.core.mission.IProgress;
import net.sakuragame.eternal.justquest.core.quest.QuestReward;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Getter
public class QuestProgress implements Comparable<QuestProgress> {

    private final String questID;
    private String missionID;
    private final IProgress progress;
    private final QuestReward reward;

    private QuestState state;

    private final Long expire;

    public QuestProgress(String questID, String missionID, IProgress progress, Long expire) {
        this(questID, missionID, progress, null, QuestState.Accepted, expire);
    }

    public QuestProgress(String questID, String missionID, IProgress progress, QuestReward reward, QuestState state, Long expire) {
        this.questID = questID;
        this.missionID = missionID;
        this.progress = progress;
        this.reward = reward;
        this.state = state;
        this.expire = expire;
    }

    public void setMissionID(String missionID) {
        this.missionID = missionID;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public String getRewardData() {
        if (this.reward == null) return null;
        return JSON.toJSONString(this.reward);
    }

    public boolean isCompleted() {
        return this.state == QuestState.Completed;
    }

    public boolean isExpire() {
        if (this.expire == -1) return false;
        return this.expire >= System.currentTimeMillis();
    }

    @Override
    public int compareTo(@NotNull QuestProgress o) {
        if (this.isCompleted() && o.isCompleted()) return 0;
        return this.isCompleted() ? -1 : 1;
    }
}
