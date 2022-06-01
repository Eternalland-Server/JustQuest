package net.sakuragame.eternal.justquest.core.hook.miner;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.kirraminer.KirraMinerAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ExploreOreEvent extends AbstractEvent {

    private final String oreID;

    public ExploreOreEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.oreID = section.getString("ore");
    }

    @Override
    public void execute(Player player) {
        KirraMinerAPI.INSTANCE.getNearestOreOfPlayer(player, this.oreID);
    }
}
