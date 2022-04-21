package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.io.UIConversationIO;
import net.sakuragame.eternal.justquest.core.data.NPCConfig;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.entity.Player;

public class ConversationManager {

    public void enter(Player player, String id) {
        NPCConfig config = JustQuest.getProfileManager().getNPCConfig(id);
        if (config == null) return;

        QuestAccount account = JustQuest.getAccountManager().getAccount(player);

        Conversation def = config.getDefaultConversation();
        for (String quest : config.getQuestConversation()) {
            if (account.getQuestProgress().containsKey(quest)) {
                String mission = account.getQuestProgress().get(quest).getMissionID();
                def = JustQuest.getProfileManager().getConversation(mission);
                break;
            }
        }

        ConversationEvent.Enter event = new ConversationEvent.Enter(player, id, def);
        event.call();
        if (event.isCancelled()) return;

        new UIConversationIO(player, id, def);
    }
}
