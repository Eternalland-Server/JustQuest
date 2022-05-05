package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.dragoncore.util.Pair;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.chain.ChainRequire;
import net.sakuragame.eternal.justquest.core.chain.ChainReward;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI.plugin;

public class ChainManager {

    private final Map<State, Conversation> conversations;
    private final Map<String, ChainRequire> chainRequires;
    private final Map<Integer, ChainReward> chainRewards;

    private final String QUEST_ID = "chain_quest";

    public ChainManager() {
        this.conversations = new HashMap<>();
        this.chainRequires = new HashMap<>();
        this.chainRewards = new LinkedHashMap<>();
    }

    public void init() {
        this.loadConversation();
        this.loadRequire();
        this.loadReward();
    }

    public void enterConversation(Player player) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
    }

    private void loadConversation() {
        File file = new File(JustQuest.getInstance().getDataFolder(), "chain/conversations.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        for (String key : yaml.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection(key);
            this.conversations.put(State.valueOf(key), JustQuest.getProfileManager().parseConversation(key, section));
        }
    }

    private void loadRequire() {
        File file = new File(plugin.getDataFolder(), "chain/require.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            String item = yaml.getString(key + ".item");
            String amount = yaml.getString(key + ".amount");
            String dungeon = yaml.getString(key + ".dungeon");
            String mobs = yaml.getString(key + ".mobs");
            int scope = yaml.getInt(key + ".scope");

            if (!amount.contains("-")) continue;
            String[] args = amount.split("-", 2);
            Pair<Integer, Integer> pair = new Pair<>(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

            this.chainRequires.put(key, new ChainRequire(item, pair, dungeon, mobs, scope));
        }
    }

    private void loadReward() {
        File file = new File(plugin.getDataFolder(), "chain/reward.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            int money = yaml.getInt(key + ".money");
            List<String> data = yaml.getStringList(key + ".items");

            this.chainRewards.put(Integer.parseInt(key), new ChainReward(money, ChainReward.parse(data)));
        }
    }

    private static enum State {
        Pending,
        Active,
        Finished
    }
}
