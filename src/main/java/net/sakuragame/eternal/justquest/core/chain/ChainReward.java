package net.sakuragame.eternal.justquest.core.chain;

import lombok.Getter;

import java.util.Map;

@Getter
public class ChainReward {

    private final int money;
    private final Map<String, Integer> items;

    public ChainReward(int money, Map<String, Integer> items) {
        this.money = money;
        this.items = items;
    }
}
