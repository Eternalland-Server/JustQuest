package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class Conversation {

    private final String ID;
    private final String npc;
    private final String complete;
    private final Map<String, Dialogue> dialogues;

    public Conversation(String ID, String complete, Map<String, Dialogue> dialogues) {
        this.ID = ID;
        this.npc = null;
        this.complete = complete;
        this.dialogues = dialogues;
    }

    public Conversation(String ID, String npc, String complete, Map<String, Dialogue> dialogues) {
        this.ID = ID;
        this.npc = npc;
        this.complete = complete;
        this.dialogues = dialogues;
    }

    public String getNPC() {
        return npc;
    }

    public Dialogue getFirstDialogue(Player player) {
        List<String> keys = new ArrayList<>(this.dialogues.keySet());
        if (keys.size() == 0) return null;

        return this.getDialogue(player, keys.get(0));
    }

    public Dialogue getDialogue(Player player, String key) {
        Dialogue dialogue = this.dialogues.get(key);

        if (dialogue == null) return null;

        if (!dialogue.meetConditions(player)) {
            if (dialogue.getThen() == null) return null;
            return this.getDialogue(player, dialogue.getThen());
        }

        return dialogue.clone();
    }
}
