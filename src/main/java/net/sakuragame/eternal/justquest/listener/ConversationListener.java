package net.sakuragame.eternal.justquest.listener;

import ink.ptms.adyeshach.api.AdyeshachAPI;
import ink.ptms.adyeshach.common.entity.EntityInstance;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import org.bukkit.Sound;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnterSound(ConversationEvent.Enter e) {
        if (e.isCancelled()) return;

        Player player = e.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 0.33f, 1);
    }

    @EventHandler
    public void onLeaveSound(ConversationEvent.Leave e) {
        Player player = e.getPlayer();
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 0.33f, 1);
    }
}
