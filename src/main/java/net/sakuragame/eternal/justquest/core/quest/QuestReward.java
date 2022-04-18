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
}
