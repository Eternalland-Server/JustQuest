package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;

import java.util.Map;

@Getter
public class Conversation {

    private final String ID;
    private final String npc;
    private final String complete;
    private final Map<String, Dialogue> dialogues;

    public Conversation(String ID, String npc, String complete, Map<String, Dialogue> dialogues) {
        this.ID = ID;
        this.npc = npc;
        this.complete = complete;
        this.dialogues = dialogues;
    }

    public String getNPC() {
        return npc;
    }

    public Dialogue getFirstDialogue() {
        return this.dialogues.values().stream().findFirst().get();
    }

    public Dialogue getDialogue(String key) {
        return this.dialogues.get(key);
    }
}
