package net.sakuragame.eternal.justquest.api.event;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.ReplayOption;
import org.bukkit.entity.Player;

public class ConversationOptionEvent {

    @Getter
    public static class Pre extends JustEvent {
        private final String npc;
        private final Conversation conversation;
        private final ReplayOption option;

        public Pre(Player who, String npc, Conversation conversation, ReplayOption option) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
            this.option = option;
        }
    }

    @Getter
    public static class Post extends JustEvent {
        private final String npc;
        private final Conversation conversation;
        private final ReplayOption option;

        public Post(Player who, String npc, Conversation conversation, ReplayOption option) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
            this.option = option;
        }
    }
}
