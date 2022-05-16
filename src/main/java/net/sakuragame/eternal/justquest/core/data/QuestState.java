package net.sakuragame.eternal.justquest.core.data;

public enum QuestState {

    Pending(-1),
    Accepted(0),
    Completed(1),
    Finished(2);

    private final int ID;

    QuestState(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public static QuestState match(int ID) {
        for (QuestState state : values()) {
            if (state.getID() == ID) return state;
        }

        return null;
    }
}
