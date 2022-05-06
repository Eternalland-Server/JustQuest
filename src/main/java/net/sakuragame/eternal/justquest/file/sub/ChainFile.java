package net.sakuragame.eternal.justquest.file.sub;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.chain.ChainReward;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChainFile {

    public static String npc;
    public static Map<Integer, ChainReward> rewards;

    private final static int[] scope = {40, 20, 0};

    public static void init() {
        YamlConfiguration yaml = JustQuest.getFileManager().getChain();

        npc = yaml.getString("npc");

        rewards = new LinkedHashMap<>();
        for (int key : scope) {
            int money = yaml.getInt("reward." + key + ".money");
            List<String> list = yaml.getStringList("reward." + key + ".items");
            Map<String, Integer> items = new LinkedHashMap<>();

            for (String s : list) {
                String[] args = s.split(" ", 2);
                items.put(args[0], Integer.parseInt(args[1]));
            }

            rewards.put(key, new ChainReward(money, items));
        }
    }

    public static int getRewardMoney(int chain) {
        for (int key : rewards.keySet()) {
            if (chain < key) continue;
            return rewards.get(key).getMoney();
        }

        return 0;
    }

    public static Map<String, Integer> getRewardItems(int chain) {
        for (int key : rewards.keySet()) {
            if (chain < key) continue;
            return rewards.get(key).getItems();
        }

        return new HashMap<>();
    }
}
