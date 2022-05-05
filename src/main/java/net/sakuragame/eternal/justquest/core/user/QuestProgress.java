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
    private final String missionID;
    private final IProgress progress;
    private QuestState state;


    public QuestProgress(String questID, String missionID, IProgress progress) {
        this(questID, missionID, progress, QuestState.Accepted);
    }

    public QuestProgress(String questID, String missionID, IProgress progress, QuestState state) {
        this.questID = questID;
        this.missionID = missionID;
        this.progress = progress;
        this.state = state;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public boolean isCompleted() {
        return this.state == QuestState.Completed;
    }

    @Override
    public int compareTo(@NotNull QuestProgress o) {
        if (this.isCompleted() && o.isCompleted()) return 0;
        return this.isCompleted() ? -1 : 1;
    }
}
