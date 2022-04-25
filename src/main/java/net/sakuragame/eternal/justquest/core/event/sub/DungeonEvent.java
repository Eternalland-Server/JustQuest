package net.sakuragame.eternal.justquest.core.event.sub;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.kirradungeon.client.KirraDungeonClientAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DungeonEvent extends AbstractEvent {

    private final int category;
    private final int sub;
    private final int index;

    public DungeonEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.category = section.getInt("category");
        this.sub = section.getInt("sub");
        this.index = section.getInt("index");
    }

    @Override
    public void execute(Player player) {
        KirraDungeonClientAPI.INSTANCE.openUI(player, this.category, this.sub, this.index);
    }
}
