package net.sakuragame.eternal.justquest.core.event.sub;

import ink.ptms.zaphkiel.ZaphkielAPI;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class GiveItemsEvent extends AbstractEvent {

    private final Map<String, Integer> items;

    public GiveItemsEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.items = new LinkedHashMap<>();

        for (String key : section.getKeys(false)) {
            int amount = section.getInt(key);
            this.items.put(key, amount);
        }
    }

    @Override
    public void execute(Player player) {
        if (items.size() == 0) return;

        Utils.giveItems(player, this.items);
    }
}
