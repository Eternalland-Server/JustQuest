package net.sakuragame.eternal.justquest.core.event.sub;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class RemoveEffectEvent extends AbstractEvent {

    private final List<String> effects;

    public RemoveEffectEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.effects = section.getStringList("effects");
    }

    @Override
    public void execute(Player player) {
        effects.forEach(k -> {
            PotionEffectType type = PotionEffectType.getByName(k.toUpperCase());
            if (type != null) {
                player.removePotionEffect(type);
            }
        });
    }
}
