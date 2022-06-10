package net.sakuragame.eternal.justquest.core.quest;

import lombok.Getter;
import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.eternal.gemseconomy.currency.EternalCurrency;
import net.sakuragame.eternal.justlevel.api.JustLevelAPI;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        if (this.exp > 0) JustLevelAPI.addExp(uuid, this.exp);
        if (this.money > 0) GemsEconomyAPI.deposit(uuid, this.money);
        if (this.coins > 0) GemsEconomyAPI.deposit(uuid, this.coins, EternalCurrency.Coins);

        Player player = Bukkit.getPlayer(uuid);
        Utils.giveItems(player, this.items);
    }

    public String getRewardDescriptions() {
        StringBuilder builder = new StringBuilder();
        if (this.money > 0) builder.append("&a金币: &f").append((int) this.money).append("  ");
        if (this.coins > 0) builder.append("&a点劵: &f").append((int) this.coins).append("  ");
        if (this.exp > 0) builder.append("&a经验: &f").append((int) this.exp).append("  ");

        return builder.toString();
    }
}
