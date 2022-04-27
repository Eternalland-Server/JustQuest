package net.sakuragame.eternal.justquest.core.event.sub;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.kirradungeon.client.KirraDungeonClientAPI;
import net.sakuragame.eternal.kirradungeon.common.KirraDungeonCommonAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DungeonEvent extends AbstractEvent {

    private final String id;

    public DungeonEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.id = section.getString("id");
    }

    @Override
    public void execute(Player player) {
        if (KirraDungeonCommonAPI.INSTANCE.getCurrentServer() != KirraDungeonCommonAPI.ServerType.CLIENT) return;
        KirraDungeonClientAPI.INSTANCE.openUI(player, this.id);
    }
}
