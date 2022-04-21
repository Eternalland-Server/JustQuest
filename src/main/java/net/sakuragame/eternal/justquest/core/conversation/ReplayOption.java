package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class ReplayOption {

    private final String ID;

    private final String text;
    private final List<String> events;
    private final String go;

    public ReplayOption(String ID, String text, List<String> events, String go) {
        this.ID = ID;
        this.text = text;
        this.events = events;
        this.go = go;
    }

    public void fireEvents(Player player) {
        if (this.events.isEmpty()) return;
        JustQuest.getQuestManager().fireEvents(player, this.events);
    }
}
