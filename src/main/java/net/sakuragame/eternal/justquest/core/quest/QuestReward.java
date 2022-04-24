package net.sakuragame.eternal.justquest.core.quest;

import lombok.Getter;

import java.util.Map;

@Getter
public class QuestReward {

    private final double exp;
    private final double money;
    private final double coins;
    private final Map<String, Integer> items;

    public QuestReward(double exp, double money, double coins, Map<String, Integer> items) {
        this.exp = exp;
        this.money = money;
        this.coins = coins;
        this.items = items;
    }

    public String getRewardDescriptions() {
        StringBuilder builder = new StringBuilder();
        if (this.money != -1) builder.append("&a金币: &f").append(this.money);
        if (this.coins != -1) builder.append("&a点劵: &f").append(this.coins);
        if (this.exp != -1) builder.append("&a经验: &f").append(this.exp);

        return builder.toString();
    }
}
