package net.sakuragame.eternal.justquest.core.data;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;

import java.util.List;

@Getter
public class NPCConfig {

    private final String ID;
    private final String name;
    private final double scale;

    private final List<String> questConversation;
    private final Conversation defaultConversation;

    public NPCConfig(String ID, String name, double scale, List<String> questConversation, Conversation defaultConversation) {
        this.ID = ID;
        this.name = name;
        this.scale = scale;
        this.questConversation = questConversation;
        this.defaultConversation = defaultConversation;
    }
}
