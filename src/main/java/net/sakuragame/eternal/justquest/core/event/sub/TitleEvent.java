package net.sakuragame.eternal.justquest.core.event.sub;

import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class TitleEvent extends AbstractEvent {

    private final String subject;
    private final String subhead;
    private final int fadeIn;
    private final int fadeOut;
    private final int duration;

    public TitleEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.subject = MegumiUtil.onReplace(section.getString("subject", ""));
        this.subhead = MegumiUtil.onReplace(section.getString("subhead", ""));
        this.fadeIn = section.getInt("fade-in", 10);
        this.fadeOut = section.getInt("fade-out", 10);
        this.duration = section.getInt("duration", 30);
    }

    @Override
    public void execute(Player player) {
        player.sendTitle(this.subject, this.subhead, this.fadeIn, this.duration, this.fadeOut);
    }
}
