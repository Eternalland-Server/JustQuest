package net.sakuragame.eternal.justquest.core.condition;

import org.bukkit.entity.Player;

public interface ICondition {

    String getID();

    boolean isNegation();

    boolean meet(Player player);
}
