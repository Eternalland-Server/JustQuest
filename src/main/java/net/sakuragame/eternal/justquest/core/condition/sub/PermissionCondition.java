package net.sakuragame.eternal.justquest.core.condition.sub;

import net.sakuragame.eternal.justquest.core.condition.AbstractCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class PermissionCondition extends AbstractCondition {

    private final String permission;

    public PermissionCondition(String ID, Boolean negation, ConfigurationSection section) {
        super(ID, negation, section);
        this.permission = section.getString("permission");
    }

    @Override
    public boolean meet(Player player) {
        if (this.isNegation()) return !player.hasPermission(this.permission);
        return player.hasPermission(this.permission);
    }
}
