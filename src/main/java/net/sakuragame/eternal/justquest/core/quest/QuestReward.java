package net.sakuragame.eternal.justquest.core.quest;

import lombok.Getter;
import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.eternal.gemseconomy.currency.EternalCurrency;
import net.sakuragame.eternal.justlevel.api.JustLevelAPI;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
        Player player = Bukkit.getPlayer(uuid);

        if (this.exp > 0) JustLevelAPI.addExp(uuid, this.exp);
        if (this.money > 0) {
            GemsEconomyAPI.deposit(uuid, this.money);
            player.sendMessage(" §8[§e+§8] §f" + this.money + " §7金币");
        }
        if (this.coins > 0) {
            GemsEconomyAPI.deposit(uuid, this.coins, EternalCurrency.Coins);
            player.sendMessage(" §8[§e+§8] §f" + this.coins + " §7点劵");
        }

        Utils.giveItems(player, this.items);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BASS, 0.4f, 1);
    }

    public String getRewardDescriptions() {
        StringBuilder builder = new StringBuilder();
        if (this.money > 0) builder.append("&6金币: &f").append((int) this.money).append("  ");
        if (this.coins > 0) builder.append("&6点劵: &f").append((int) this.coins).append("  ");
        if (this.exp > 0) builder.append("&6经验: &f").append((int) this.exp).append("  ");

        return builder.toString();
    }
}
