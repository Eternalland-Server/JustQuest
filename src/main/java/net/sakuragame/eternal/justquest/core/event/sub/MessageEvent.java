package net.sakuragame.eternal.justquest.core.event.sub;

import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageEvent extends AbstractEvent {

    private final List<String> contents;

    public MessageEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.contents = MegumiUtil.onReplace(section.getStringList("contents"));
    }

    @Override
    public void execute(Player player) {
        this.contents.forEach(player::sendMessage);
    }
}
