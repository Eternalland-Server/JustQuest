package net.sakuragame.eternal.justquest.core.condition;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public abstract class AbstractCondition implements ICondition {

    private final String ID;
    private final boolean negation;

    public AbstractCondition(String ID, Boolean negation, ConfigurationSection section) {
        this.ID = ID;
        this.negation = negation;
    }
}
