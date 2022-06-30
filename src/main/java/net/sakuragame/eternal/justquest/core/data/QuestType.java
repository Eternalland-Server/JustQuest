package net.sakuragame.eternal.justquest.core.data;

import lombok.Getter;

@Getter
public enum QuestType {

    MQ(1, "◧", "ui/quest/1.png", "&8[&6主线&8]", true), // 主线
    SQ(2, "◨", "ui/quest/2.png", "&8[&b支线&8]", true), // 支线
    DQ(3, "◩", "ui/quest/3.png", "&8[&a每日&8]", false), // 每日
    CQ(4, "◪", "ui/quest/4.png", "&8[&4跑环&8]", false); // 跑环

    private final int ID;
    private final String symbol;
    private final String texture;
    private final String prefix;
    private final boolean once;

    QuestType(int ID, String symbol, String texture, String prefix, boolean once) {
        this.ID = ID;
        this.symbol = symbol;
        this.texture = texture;
        this.prefix = prefix;
        this.once = once;
    }

    public static QuestType match(int ID) {
        for (QuestType type : values()) {
            if (type.getID() == ID) return type;
        }

        return null;
    }
}
