package net.sakuragame.eternal.justquest.core.event;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public abstract class AbstractEvent implements IEvent {

    private final String ID;

    public AbstractEvent(String ID, ConfigurationSection section) {
        this.ID = ID;
    }
}
