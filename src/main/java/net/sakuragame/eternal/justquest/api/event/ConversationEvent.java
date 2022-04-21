package net.sakuragame.eternal.justquest.api.event;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import org.bukkit.entity.Player;

public class ConversationEvent {

    @Getter
    public static class Enter extends JustEvent {

        private final String npc;
        private final Conversation conversation;

        public Enter(Player who, String npc, Conversation conversation) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
        }

        public String getNPC() {
            return npc;
        }
    }

    @Getter
    public static class Leave extends JustEvent {

        private final String npc;
        private final Conversation conversation;

        public Leave(Player who, String npc, Conversation conversation) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
        }

        public String getNPC() {
            return npc;
        }
    }

    @Getter
    public static class Complete extends JustEvent {

        private final String npc;
        private final Conversation conversation;

        public Complete(Player who, String npc, Conversation conversation) {
            super(who);
            this.npc = npc;
            this.conversation = conversation;
        }

        public String getNPC() {
            return npc;
        }
    }
}
