package net.sakuragame.eternal.justquest.listener;

import net.citizensnpcs.api.CitizensAPI;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ConversationListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEnter(ConversationEvent.Enter e) {
        if (e.isCancelled()) return;

        Player player = e.getPlayer();
        int npc = e.getNpc();

        UUID uuid = CitizensAPI.getNPCRegistry().getById(npc).getEntity().getUniqueId();
        double scale = JustQuest.getProfileManager().getNPCConfig(npc).getScale();

        JustQuest.getUiManager().sendConvDoll(player, uuid, scale);
    }
}
