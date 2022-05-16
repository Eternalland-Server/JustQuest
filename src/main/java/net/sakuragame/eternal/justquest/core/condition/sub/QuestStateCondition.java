package net.sakuragame.eternal.justquest.core.condition.sub;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.condition.AbstractCondition;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class QuestStateCondition extends AbstractCondition {

    private final String questID;
    private final int stateID;

    public QuestStateCondition(String ID, Boolean negation, ConfigurationSection section) {
        super(ID, negation, section);
        this.questID = section.getString("quest");
        this.stateID = section.getInt("state");
    }

    @Override
    public boolean meet(Player player) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        if (this.isNegation()) return account.getQuestState(this.questID).getID() != stateID;
        return account.getQuestState(this.questID).getID() == stateID;
    }
}
