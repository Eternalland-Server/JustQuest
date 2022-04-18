package net.sakuragame.eternal.justquest.listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CitizensListener implements Listener {

    @EventHandler
    public void onRight(NPCRightClickEvent e) {
        Player player = e.getClicker();
        NPC npc = e.getNPC();
        int id = npc.getId();

        JustQuest.getConversationManager().enter(player, id);
    }
}
