package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

@Getter
public class Dialogue {

    private final String ID;

    private final List<String> response;
    private final List<String> events;
    private final Map<String, ReplayOption> options;

    public Dialogue(String ID, List<String> response, List<String> events, Map<String, ReplayOption> options) {
        this.ID = ID;
        this.response = response;
        this.events = events;
        this.options = options;
    }

    public ReplayOption getOption(String key) {
        return this.options.get(key);
    }

    public void fireEvents(Player player) {
        if (events.isEmpty()) return;
        JustQuest.getQuestManager().fireEvents(player, this.events);
    }
}
