package net.sakuragame.eternal.justquest.core.chain;

import net.sakuragame.eternal.justquest.core.quest.QuestReward;

import java.util.UUID;

public class ChainTask {

    private final UUID uuid;
    private final String require;
    private final QuestReward reward;

    public ChainTask(UUID uuid, String require, QuestReward reward) {
        this.uuid = uuid;
        this.require = require;
        this.reward = reward;
    }
}
