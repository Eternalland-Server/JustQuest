package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Dialogue implements Cloneable {

    private final String ID;
    private final List<String> conditions;
    private final String then;
    private List<String> response;
    private final List<String> events;
    private Map<String, ReplayOption> options;

    public Dialogue(String ID, List<String> response, Map<String, ReplayOption> options) {
        this.ID = ID;
        this.response = response;
        this.options = options;
        this.conditions = new ArrayList<>();
        this.then = null;
        this.events = new ArrayList<>();
    }

    public Dialogue(String ID, List<String> conditions, String then, List<String> response, List<String> events, Map<String, ReplayOption> options) {
        this.ID = ID;
        this.conditions = conditions;
        this.then = then;
        this.response = response;
        this.events = events;
        this.options = options;
    }

    public ReplayOption getOption(String key) {
        return this.options.get(key);
    }

    public void fireEvents(Player player) {
        if (this.events.isEmpty()) return;
        JustQuest.getQuestManager().fireEvents(player, this.events);
    }

    public boolean meetConditions(Player player) {
        if (this.conditions.isEmpty()) return true;
        return JustQuest.getQuestManager().meetConditions(player, this.conditions);
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
