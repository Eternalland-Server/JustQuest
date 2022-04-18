package net.sakuragame.eternal.justquest.core.quest.sub;

import net.sakuragame.eternal.justquest.core.quest.QuestReward;
import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;

import java.util.List;
import java.util.UUID;

public class SIdeQuest extends AbstractQuest {

    public SIdeQuest(String ID, String name, List<String> missions, QuestReward reward) {
        super(ID, name, missions, reward);
    }

    @Override
    public QuestType getType() {
        return QuestType.SQ;
    }

    @Override
    public boolean isAllowCancel() {
        return false;
    }

    @Override
    public void award(UUID uuid) {

    }
}
