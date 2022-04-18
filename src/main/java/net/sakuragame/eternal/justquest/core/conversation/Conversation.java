package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;

import java.util.Map;

@Getter
public class Conversation {

    private final String ID;

    private final Map<String, Dialogue> dialogues;

    public Conversation(String ID, Map<String, Dialogue> dialogues) {
        this.ID = ID;
        this.dialogues = dialogues;
    }

    public Dialogue getFirstDialogue() {
        return this.dialogues.values().stream().findFirst().get();
    }

    public Dialogue getDialogue(String key) {
        return this.dialogues.get(key);
    }
}
