package net.sakuragame.eternal.justquest.api.event;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import org.bukkit.entity.Player;

public class ConversationEvent {

    @Getter
    public static class Enter extends JustEvent {

        private final int npc;
        private final Conversation conversation;

        public Enter(Player who, int npc, Conversation conversation) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
        }
    }

    @Getter
    public static class Leave extends JustEvent {

        private final int npc;
        private final Conversation conversation;

        public Leave(Player who, int npc, Conversation conversation) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
        }
    }

    @Getter
    public static class Complete extends JustEvent {

        private final int npc;
        private final Conversation conversation;

        public Complete(Player who, int npc, Conversation conversation) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
        }
    }
}
