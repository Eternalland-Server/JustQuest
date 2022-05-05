package net.sakuragame.eternal.justquest.core.mission;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;

import java.util.UUID;

@Getter
public abstract class AbstractProgress implements IProgress {

    private final UUID UUID;
    private final String questID;

    public AbstractProgress(UUID uuid, String questID) {
        this.UUID = uuid;
        this.questID = questID;
    }

    @Override
    public void update() {
        QuestAccount account = JustQuest.getAccountManager().getAccount(this.UUID);
        if (account == null) return;
        if (!account.getTrace().equals(this.questID)) return;
        account.updateTraceBar();
    }
}
