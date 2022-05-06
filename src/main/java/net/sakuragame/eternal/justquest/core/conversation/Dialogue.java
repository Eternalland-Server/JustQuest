package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Dialogue implements Cloneable {

    private final String ID;

    private List<String> response;
    private final List<String> events;
    private Map<String, ReplayOption> options;

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

    public void setResponse(List<String> response) {
        this.response = response;
    }

    public void setOptions(Map<String, ReplayOption> options) {
        this.options = options;
    }

    @Override
    public Dialogue clone() {
        try {
            Dialogue clone = (Dialogue) super.clone();
            Map<String, ReplayOption> map = new LinkedHashMap<>();
            this.getOptions().forEach((k, v) -> map.put(k, v.clone()));
            clone.setOptions(map);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
