package net.sakuragame.eternal.justquest.core.data;

import lombok.Getter;

@Getter
public enum QuestType {

    MQ(1, "◧", "ui/quest/1.png"), // 主线
    SQ(2, "◨", "ui/quest/2.png"), // 支线
    DQ(3, "◩", "ui/quest/3.png"), // 每日
    CQ(4, "◪", "ui/quest/4.png"); // 跑环

    private final int ID;
    private final String symbol;
    private final String texture;

    QuestType(int ID, String symbol, String texture) {
        this.ID = ID;
        this.symbol = symbol;
        this.texture = texture;
    }

    public static QuestType match(int ID) {
        for (QuestType type : values()) {
            if (type.getID() == ID) return type;
        }

        return null;
    }
}
