package net.sakuragame.eternal.justquest.core.event.sub;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.waypoints.api.WaypointsAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class WaypointsEvent extends AbstractEvent {

    private final String id;

    public WaypointsEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.id = section.getString("id");
    }

    @Override
    public void execute(Player player) {
        WaypointsAPI.navPointer(player, id, Arrays.asList("§6§l前往", "§f(%distance%m)"));
        player.sendTitle("", "§3§l已开始导航", 10, 20, 10);
    }
}
