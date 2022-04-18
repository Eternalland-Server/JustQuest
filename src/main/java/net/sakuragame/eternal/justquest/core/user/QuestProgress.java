package net.sakuragame.eternal.justquest.core.user;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.data.QuestState;

@Getter
public class QuestProgress {

    private final String questID;
    private String missionID;
    private String data;

    private QuestState state;

    public QuestProgress(String questID, String missionID, String data) {
        this(questID, missionID, data, QuestState.Accepted);
    }

    public QuestProgress(String questID, String missionID, String data, QuestState state) {
        this.questID = questID;
        this.missionID = missionID;
        this.data = data;

        this.state = state;
    }

    public void setMissionID(String missionID) {
        this.missionID = missionID;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setState(QuestState state) {
        this.state = state;
    }

    public boolean isCompleted() {
        return this.state == QuestState.Completed;
    }
}
