package net.sakuragame.eternal.justquest.listener;

import ink.ptms.adyeshach.api.AdyeshachAPI;
import ink.ptms.adyeshach.common.entity.EntityInstance;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ConversationListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnter(ConversationEvent.Enter e) {
        if (e.isCancelled()) return;

        Player player = e.getPlayer();
        String id = e.getNPC();

        if (id.equals("$self")) {
            JustQuest.getUiManager().sendConvDoll(player, player.getUniqueId(), 3);
            return;
        }

        EntityInstance instance = AdyeshachAPI.INSTANCE.getEntityFromId(id, player);
        if (instance == null) return;

        double scale = JustQuest.getProfileManager().getNPCConfig(id).getScale();

        JustQuest.getUiManager().sendConvDoll(player, instance.getNormalizeUniqueId(), scale);
    }
}
