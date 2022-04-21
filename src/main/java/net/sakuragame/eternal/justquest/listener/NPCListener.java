package net.sakuragame.eternal.justquest.listener;

import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCListener implements Listener {

    @EventHandler
    public void onRight(AdyeshachEntityInteractEvent e) {
        Player player = e.getPlayer();
        if (!e.isMainHand()) return;

        String id = e.getEntity().getId();
        JustQuest.getConversationManager().enter(player, id);
    }
}
