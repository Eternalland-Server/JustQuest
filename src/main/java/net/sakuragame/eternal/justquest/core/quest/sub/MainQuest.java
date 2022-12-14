package net.sakuragame.eternal.justquest.core.quest.sub;

import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;
import net.sakuragame.eternal.justquest.core.quest.QuestReward;

import java.util.List;
import java.util.UUID;

public class MainQuest extends AbstractQuest {

    public MainQuest(String ID, String name, Boolean silent, List<String> descriptions, List<String> missions, String next, QuestReward reward) {
        super(ID, name, silent, descriptions, missions, next, reward);
    }

    @Override
    public QuestType getType() {
        return QuestType.MQ;
    }

    @Override
    public boolean isAllowCancel() {
        return false;
    }
}
