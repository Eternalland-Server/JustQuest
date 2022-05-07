package net.sakuragame.eternal.justquest.listener;

import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import net.sakuragame.eternal.justquest.core.ChainManager;
import net.sakuragame.eternal.justquest.core.chain.ChainRequire;
import net.sakuragame.eternal.justquest.core.conversation.Dialogue;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.file.sub.ChainFile;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChainListener implements Listener {

    @EventHandler
    public void onChain(AdyeshachEntityInteractEvent e) {
        Player player = e.getPlayer();
        if (!e.isMainHand()) return;

        String id = e.getEntity().getId();
        if (!id.equals(ChainFile.npc)) return;

        JustQuest.getChainManager().enter(player);
    }

    @EventHandler
    public void onContents(ConversationEvent.Contents e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!e.getConversation().getID().equals(ChainManager.State.Pending.getConversationID())) return;

        String cache = JustQuest.getChainManager().getCache(uuid);
        if (cache == null) return;

        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        int chain = account.getChain();

        ChainRequire require = JustQuest.getChainManager().getRequire(cache);
        String name = Utils.getItemName(require.getItem());

        Dialogue dialogue = e.getDialogue();
        List<String> replace = new ArrayList<>();
        for (String s : dialogue.getResponse()) {
            replace.add(s.replace("<item>", name));
        }
        dialogue.setResponse(replace);

        dialogue.getOptions().values().forEach(k -> k.setText(k.getText().replace("<chain>", (chain + 1) + "")));
    }

    @EventHandler
    public void onPending(ConversationEvent.Complete e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!e.getConversation().getID().equals(ChainManager.State.Pending.getConversationID())) return;

        String cache = JustQuest.getChainManager().getCache(uuid);
        if (cache == null) return;

        JustQuest.getChainManager().allotQuest(player);
    }

    @EventHandler
    public void onComplete(ConversationEvent.Complete e) {
        Player player = e.getPlayer();

        if (!e.getConversation().getID().equals(ChainManager.State.Completed.getConversationID())) return;

        JustQuest.getUiManager().openQuest(player);
    }
}
