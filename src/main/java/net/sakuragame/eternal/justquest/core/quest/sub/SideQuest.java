package net.sakuragame.eternal.justquest.core.quest.sub;

import net.sakuragame.eternal.justquest.core.quest.QuestReward;
import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;

import java.util.List;

public class SideQuest extends AbstractQuest {

    public SideQuest(String ID, String name, Boolean silent, List<String> descriptions, List<String> missions, String next, QuestReward reward) {
        super(ID, name, silent, descriptions, missions, next, reward);
    }

    @Override
    public QuestType getType() {
        return QuestType.SQ;
    }

    @Override
    public boolean isAllowCancel() {
        return false;
    }
}
