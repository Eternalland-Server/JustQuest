package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class ReplayOption implements Cloneable {

    private final String ID;

    private String text;
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

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public ReplayOption clone() {
        try {
            return (ReplayOption) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
