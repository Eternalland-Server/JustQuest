package net.sakuragame.eternal.justquest.api.event;

import net.sakuragame.eternal.justlevel.api.event.JustEvent;
import org.bukkit.entity.Player;

public class ChainQuestSubmitEvent extends JustEvent {

    public ChainQuestSubmitEvent(Player who) {
        super(who);
    }
}
