package net.sakuragame.eternal.justquest.core.quest;

import ink.ptms.zaphkiel.ZaphkielAPI;
import lombok.Getter;
import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.eternal.gemseconomy.currency.EternalCurrency;
import net.sakuragame.eternal.justlevel.api.JustLevelAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

@Getter
public class QuestReward {

    private final double exp;
    private final double money;
    private final double coins;
    private final Map<String, Integer> items;

    public QuestReward(double exp, double money, double coins, Map<String, Integer> items) {
        this.exp = exp;
        this.money = money;
        this.coins = coins;
        this.items = items;
    }

    public void apply(UUID uuid) {
        JustLevelAPI.addExp(uuid, this.exp);
        GemsEconomyAPI.deposit(uuid, this.money);
        GemsEconomyAPI.deposit(uuid, this.coins, EternalCurrency.Coins);

        Player player = Bukkit.getPlayer(uuid);
        items.forEach((k, v) -> {
            ItemStack item = ZaphkielAPI.INSTANCE.getItemStack(k, player);
            if (item != null) {
                item.setAmount(v);
                player.getInventory().addItem(item);
            }
        });
    }

    public String getRewardDescriptions() {
        StringBuilder builder = new StringBuilder();
        if (this.money != -1) builder.append("&a金币: &f").append((int) this.money).append("  ");
        if (this.coins != -1) builder.append("&a点劵: &f").append((int) this.coins).append("  ");
        if (this.exp != -1) builder.append("&a经验: &f").append((int) this.exp).append("  ");

        return builder.toString();
    }
}
