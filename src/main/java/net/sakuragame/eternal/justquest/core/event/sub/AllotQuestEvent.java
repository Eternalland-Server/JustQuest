package net.sakuragame.eternal.justquest.core.event.sub;

import net.sakuragame.eternal.justquest.api.JustQuestAPI;
import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class AllotQuestEvent extends AbstractEvent {

    private final String ID;

    public AllotQuestEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.ID = section.getString("id");
    }

    @Override
    public void execute(Player player) {
        JustQuestAPI.allotQuest(player, this.ID);
    }
}
