package net.sakuragame.eternal.justquest.core.chain;

import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;
import net.sakuragame.eternal.justquest.core.quest.QuestReward;

import java.util.List;

public class ChainQuest extends AbstractQuest {

    public ChainQuest(String ID, String name, List<String> descriptions, List<String> missions, String next, QuestReward reward) {
        super(ID, name, descriptions, missions, next, reward);
    }

    @Override
    public QuestType getType() {
        return QuestType.CQ;
    }

    @Override
    public boolean isAllowCancel() {
        return false;
    }
}
