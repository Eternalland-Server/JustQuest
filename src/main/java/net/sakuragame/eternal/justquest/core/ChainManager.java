package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.dragoncore.util.Pair;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import net.sakuragame.eternal.justquest.core.chain.ChainRequire;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.io.UIConversationIO;
import net.sakuragame.eternal.justquest.core.mission.sub.ChainMission;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.quest.sub.ChainQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.file.sub.ChainFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ChainManager {

    private final Map<UUID, String> cache;

    private final Map<State, Conversation> conversations;
    private final Map<String, ChainRequire> chainRequires;

    public final static String QUEST_ID = "chain_quest";
    public final static String MISSION_ID = "chain_mission";

    public ChainManager() {
        this.cache = new HashMap<>();
        this.conversations = new HashMap<>();
        this.chainRequires = new HashMap<>();
    }

    public void init() {
        this.loadConversation();
        this.loadRequire();
        this.register();

        JustQuest.getInstance().getLogger().info("loaded chain " + this.chainRequires.size() + " requirement");
    }

    private void register() {
        JustQuest.getProfileManager().registerQuest(QUEST_ID,
                new ChainQuest(QUEST_ID, "&f&l商行跑环&7&l(%current%/60)", Collections.singletonList("&f商行老板 &a钱三万 &f委托的收集任务"))
        );

        JustQuest.getProfileManager().registerMission(MISSION_ID,
                new ChainMission(MISSION_ID, "chain", Arrays.asList(
                        "&a交付所需材料给商行老板",
                        "⊔&f区域: &7<dungeon>",
                        "⊒&f怪物: &7<mobs>"
                ))
        );
    }

    public String getCache(UUID uuid) {
        return this.cache.get(uuid);
    }

    public String takeCache(UUID uuid) {
        return this.cache.remove(uuid);
    }

    public void removeCache(UUID uuid) {
        this.cache.remove(uuid);
    }

    public ChainRequire getRequire(String key) {
        return this.chainRequires.get(key);
    }

    public ChainRequire getRandom(int chain) {
        List<ChainRequire> conform = this.chainRequires.values()
                .stream().filter(k -> k.getScope() <= chain)
                .collect(Collectors.toList());

        Random random = new Random();
        int index = random.nextInt(conform.size());

        return conform.get(index);
    }

    public void enter(Player player) {
        UUID uuid = player.getUniqueId();
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        QuestProgress progress = account.getProgresses().get(QUEST_ID);
        if (progress != null) {
            if (progress.isCompleted()) {
                this.openConversation(player, State.Completed);
            }
            else {
                this.openConversation(player, State.Active);
            }
            return;
        }

        int chain = account.getChain();
        if (chain == 60) {
            this.openConversation(player, State.Done);
            return;
        }

        if (!this.cache.containsKey(uuid)) {
            String requireID = this.getRandom(chain).getID();
            this.cache.put(player.getUniqueId(), requireID);
        }

        this.openConversation(player, State.Pending);
    }

    private void openConversation(Player player, State state) {
        Conversation conv = conversations.get(state);

        ConversationEvent.Enter event = new ConversationEvent.Enter(player, ChainFile.npc, conv);
        event.call();
        if (event.isCancelled()) return;

        new UIConversationIO(player, ChainFile.npc, conv);
    }

    public void allotQuest(Player player) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(player);
        int chain = account.getChain();
        if (chain == 60) return;

        if (!this.cache.containsKey(player.getUniqueId())) {
            String requireID = this.getRandom(chain).getID();
            this.cache.put(player.getUniqueId(), requireID);
        }

        IQuest quest = JustQuest.getProfileManager().getQuest(QUEST_ID);
        quest.allot(player.getUniqueId());
    }

    private void loadConversation() {
        File file = new File(JustQuest.getInstance().getDataFolder(), "chain/conversations.yml");
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        for (String key : yaml.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection(key);
            State state = State.valueOf(key);
            this.conversations.put(state, JustQuest.getProfileManager().parseConversation(state.getConversationID(), section));
        }
    }

    private void loadRequire() {
        File file = new File(JustQuest.getInstance().getDataFolder(), "chain/requirement.yml");
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

            this.chainRequires.put(key, new ChainRequire(key, item, pair, dungeon, mobs, scope));
        }
    }

    public enum State {
        Pending,
        Active,
        Completed,
        Done;

        public String getConversationID() {
            return "Chain_" + this.name();
        }
    }
}
