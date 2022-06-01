package net.sakuragame.eternal.justquest.core.event.sub;

import ink.ptms.adyeshach.api.AdyeshachAPI;
import ink.ptms.adyeshach.common.entity.EntityInstance;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.UUID;

public class AnimationPlayEvent extends AbstractEvent {

    private final String node;

    public AnimationPlayEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.node = section.getString("node");
    }

    @Override
    public void execute(Player player) {
        UUID uuid = player.getUniqueId();
        String cache = JustQuest.getConversationManager().getCache(uuid);
        if (cache == null) return;

        EntityInstance npc = AdyeshachAPI.INSTANCE.getEntityFromId(cache, player);
        if (npc == null) return;

        PacketSender.setModelEntityAnimation(
                Collections.singletonList(player),
                npc.getNormalizeUniqueId(),
                this.node,
                0
        );
    }
}
