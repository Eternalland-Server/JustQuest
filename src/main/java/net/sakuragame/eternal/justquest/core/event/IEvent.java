package net.sakuragame.eternal.justquest.core.event;

import org.bukkit.entity.Player;

public interface IEvent {

    String getID();

    void execute(Player player);

}
