package net.sakuragame.eternal.justquest.core.data;

import lombok.Getter;

@Getter
public enum QuestType {

    MQ(1, "◧", "ui/quest/1.png", true), // 主线
    SQ(2, "◨", "ui/quest/2.png", true), // 支线
    DQ(3, "◩", "ui/quest/3.png", false), // 每日
    CQ(4, "◪", "ui/quest/4.png", false); // 跑环

    private final int ID;
    private final String symbol;
    private final String texture;
    private final boolean once;

    QuestType(int ID, String symbol, String texture, boolean once) {
        this.ID = ID;
        this.symbol = symbol;
        this.texture = texture;
        this.once = once;
    }

    public static QuestType match(int ID) {
        for (QuestType type : values()) {
            if (type.getID() == ID) return type;
        }

        return null;
    }
}
