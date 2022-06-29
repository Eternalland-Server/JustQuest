package net.sakuragame.eternal.justquest.api.event;

import lombok.Getter;
import net.sakuragame.eternal.justquest.api.JustEvent;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.entity.Player;

@Getter
public class QuestAccountLoadEvent extends JustEvent {

    private final QuestAccount account;

    public QuestAccountLoadEvent(Player who, QuestAccount account) {
        super(who);
        this.account = account;
    }
}
