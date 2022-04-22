package net.sakuragame.eternal.justquest.listener;

import eos.moe.armourers.api.DragonAPI;
import ink.ptms.adyeshach.api.AdyeshachAPI;
import ink.ptms.adyeshach.api.event.AdyeshachEntityInteractEvent;
import ink.ptms.adyeshach.api.event.AdyeshachPlayerJoinEvent;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.data.ExhibitNPC;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class NPCListener implements Listener {

    @EventHandler
    public void onRight(AdyeshachEntityInteractEvent e) {
        Player player = e.getPlayer();
        if (!e.isMainHand()) return;

        String id = e.getEntity().getId();
        JustQuest.getConversationManager().enter(player, id);
    }

    @EventHandler
    public void onVisible(AdyeshachPlayerJoinEvent event) {
        if (JustQuest.getProfileManager().getExhibitNPC().size() == 0) return;
        Scheduler.runAsync(new BukkitRunnable() {
            @Override
            public void run() {
                AdyeshachAPI.INSTANCE.getEntityManagerPublic().getEntities().forEach(entity -> {
                    UUID normalizeUniqueId = entity.getNormalizeUniqueId();
                    String npcID = entity.getId();
                    ExhibitNPC npc = JustQuest.getProfileManager().getExhibitNPC(npcID);
                    if (npc == null) {
                        return;
                    }
                    DragonAPI.setEntitySkin(normalizeUniqueId, npc.getClothes());
                });
            }
        });
    }

}
