package net.sakuragame.eternal.justquest.core.hook.store;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.juststore.api.JustStoreAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MerchantEvent extends AbstractEvent {

    private final String ID;

    public MerchantEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.ID = section.getString("id");
    }

    @Override
    public void execute(Player player) {
        JustStoreAPI.openMerchant(player, ID);
    }
}
