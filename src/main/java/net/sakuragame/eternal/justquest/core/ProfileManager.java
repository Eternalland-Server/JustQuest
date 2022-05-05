package net.sakuragame.eternal.justquest.core;

import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.dragoncore.util.Pair;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.chain.ChainRequire;
import net.sakuragame.eternal.justquest.core.chain.ChainReward;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.Dialogue;
import net.sakuragame.eternal.justquest.core.conversation.ReplayOption;
import net.sakuragame.eternal.justquest.core.data.ExhibitNPC;
import net.sakuragame.eternal.justquest.core.data.NPCConfig;
import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import net.sakuragame.eternal.justquest.core.event.IEvent;
import net.sakuragame.eternal.justquest.core.event.sub.*;
import net.sakuragame.eternal.justquest.core.hook.store.MerchantEvent;
import net.sakuragame.eternal.justquest.core.hook.store.StoreHook;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.hook.PluginHook;
import net.sakuragame.eternal.justquest.core.hook.party.PartyHook;
import net.sakuragame.eternal.justquest.core.mission.sub.*;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.quest.QuestReward;
import net.sakuragame.eternal.justquest.core.quest.sub.MainQuest;
import net.sakuragame.eternal.justquest.core.quest.sub.SIdeQuest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ProfileManager {
    
    private final JustQuest plugin;

    private Map<QuestType, Class<? extends AbstractQuest>> questPreset;
    private Map<String, Class<? extends AbstractMission>> missionPreset;
    private Map<String, Class<? extends AbstractEvent>> eventPreset;

    private final List<PluginHook> hooks;

    private Map<String, NPCConfig> npcConfig;
    private Map<String, AbstractQuest> quests;
    private Map<String, AbstractMission> missions;
    private Map<String, AbstractEvent> events;
    private Map<String, Conversation> conversations;

    private Map<String, ExhibitNPC> exhibitNPC;

    private Map<String, ChainRequire> chainRequire;
    private Map<Integer, ChainReward> chainReward;

    
    public ProfileManager(JustQuest plugin) {
        this.plugin = plugin;
        this.hooks = new ArrayList<>();
        this.hooks.add(new PartyHook());
        this.hooks.add(new StoreHook());
    }

    public void init() {
        this.questPreset = new HashMap<>();
        this.missionPreset = new HashMap<>();
        this.eventPreset = new HashMap<>();

        this.npcConfig = new HashMap<>();
        this.quests = new HashMap<>();
        this.missions = new HashMap<>();
        this.events = new HashMap<>();
        this.conversations = new HashMap<>();

        this.exhibitNPC = new HashMap<>();

        this.chainRequire = new HashMap<>();
        this.chainReward = new LinkedHashMap<>();

        this.registerQuestPreset();
        this.registerMissionPreset();
        this.registerEventPreset();

        this.loadNPC();
        this.loadQuest();
        this.loadEvent();
        this.loadMissionHook();
        this.loadExhibitNPC();
        this.loadChain();

        plugin.getLogger().info("loaded " + npcConfig.size() + " npc");
        plugin.getLogger().info("loaded " + conversations.size() + " conversations");
        plugin.getLogger().info("loaded " + quests.size() + " quests");
        plugin.getLogger().info("loaded " + missions.size() + " missions");
        plugin.getLogger().info("loaded " + events.size() + " events");
    }

    public NPCConfig getNPCConfig(String ID) {
        return this.npcConfig.get(ID);
    }

    public ExhibitNPC getExhibitNPC(String ID) {
        return this.exhibitNPC.get(ID);
    }

    public IQuest getQuest(String key) {
        return this.quests.get(key);
    }

    public IMission getMission(String key) {
        return this.missions.get(key);
    }
    
    public IEvent getEvent(String key) {
        return this.events.get(key);
    }

    public Conversation getConversation(String key) {
        return this.conversations.get(key);
    }

    public QuestType getType(String key) {
        return this.getQuest(key).getType();
    }

    public Set<String> getExhibitNPC() {
        return this.exhibitNPC.keySet();
    }

    private void registerQuestPreset() {
        this.registerQuestPreset(QuestType.MQ, MainQuest.class);
        this.registerQuestPreset(QuestType.SQ, SIdeQuest.class);
    }

    private void registerMissionPreset() {
        this.registerMissionPreset("conversation", ConversationMission.class);
        this.registerMissionPreset("mob_killer", MobKillerMission.class);
        this.registerMissionPreset("learn_ability", LearnAbilityMission.class);
        this.registerMissionPreset("consume", ConsumeMission.class);
        this.registerMissionPreset("dungeon", DungeonMission.class);
        this.registerMissionPreset("collect", CollectMission.class);
        this.registerMissionPreset("smelter", SmelterMission.class);
        this.registerMissionPreset("store_trade", StoreTradeMission.class);
        this.registerMissionPreset("identify", IdentifyMission.class);
        this.registerMissionPreset("level_up", LevelUpMission.class);
        this.registerMissionPreset("equip_suit", EquipSuitMission.class);
        this.registerMissionPreset("elevate_realm", ElevateRealmMission.class);
    }
    
    private void registerEventPreset() {
        this.registerEventPreset("give_items", GiveItemsEvent.class);
        this.registerEventPreset("merchant", MerchantEvent.class);
        this.registerEventPreset("command", CommandEvent.class);
        this.registerEventPreset("message", MessageEvent.class);
        this.registerEventPreset("title", TitleEvent.class);
        this.registerEventPreset("waypoints", WaypointsEvent.class);
        this.registerEventPreset("dungeon", DungeonEvent.class);
        this.registerEventPreset("remove_effect", RemoveEffectEvent.class);
    }

    public void registerQuestPreset(QuestType type, Class<? extends AbstractQuest> questPreset) {
        this.questPreset.put(type, questPreset);
    }

    public void registerMissionPreset(String key, Class<? extends AbstractMission> missionPreset) {
        this.missionPreset.put(key, missionPreset);
    }
    
    public void registerEventPreset(String key, Class<? extends AbstractEvent> eventPreset) {
        this.eventPreset.put(key, eventPreset);
    }

    private void loadNPC() {
        File dir = new File(plugin.getDataFolder(), "npc");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).filter(k -> k.getName().endsWith(".yml")).forEach(this::parseNPCConfigFile);
    }

    private void loadQuest() {
        File dir = new File(plugin.getDataFolder(), "quest");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).filter(File::isDirectory).forEach(sub -> {
            if (!sub.getName().startsWith("#")) {
                this.parseQuestFile(new File(sub, "quest.yml"));
                this.parseMissionFile(new File(sub, "missions.yml"));
                this.parseConversationFile(new File(sub, "conversations.yml"));
                this.parseEventFile(new File(sub, "events.yml"));
            }
        });
    }
    
    private void loadEvent() {
        File dir = new File(plugin.getDataFolder(), "event");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).filter(k -> k.getName().endsWith(".yml")).forEach(this::parseEventFile);
    }

    private void loadMissionHook() {
        for (PluginHook hook : this.hooks) {
            if (Bukkit.getPluginManager().getPlugin(hook.getPlugin()) == null) continue;
            hook.register();
        }
    }

    private void loadExhibitNPC() {
        File dir = new File(plugin.getDataFolder(), "exhibit");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).filter(k -> k.getName().endsWith(".yml")).forEach(k -> {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(k);
            for (String key : yaml.getKeys(false)) {
                String name = yaml.getString(key + ".name");
                List<String> desc = yaml.getStringList(key + ".descriptions");
                List<String> clothes = yaml.getStringList(key + ".clothes");
                this.exhibitNPC.put(key, new ExhibitNPC(key, name, desc, clothes));
            }
        });
    }

    private void parseNPCConfigFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        String id = yaml.getString("id");
        String name = yaml.getString("name");
        double scale = yaml.getDouble("scale", 2);
        List<String> questConversation = yaml.getStringList("quest-conversation");
        Conversation defaultConversation = this.parseConversation("_", yaml.getConfigurationSection("default-conversation"));

        this.npcConfig.put(id, new NPCConfig(id, name, scale, questConversation, defaultConversation));
    }

    private void parseQuestFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String id = yaml.getString("id");
        String name = MegumiUtil.onReplace(yaml.getString("name"));
        QuestType type = QuestType.valueOf(yaml.getString("type").toUpperCase());
        List<String> descriptions = yaml.getStringList("descriptions");
        List<String> missions = yaml.getStringList("missions");
        String next = yaml.getString("next");

        double exp = yaml.getDouble("reward.exp", -1);
        double money = yaml.getDouble("reward.money", -1);
        int coins = yaml.getInt("reward.coins", -1);
        List<String> items = yaml.getStringList("reward.items");
        Map<String, Integer> map = new HashMap<>();
        items.forEach(s -> {
            String[] part = s.split(" ", 2);
            if (part.length == 2) {
                map.put(part[0], Integer.parseInt(part[1]));
            }
        });

        QuestReward reward = new QuestReward(exp, money, coins, map);

        try {
            Class<? extends AbstractQuest> preset = this.questPreset.get(type);
            if (preset == null) return;

            this.quests.put(id, preset
                    .getConstructor(String.class, String.class, List.class, List.class, String.class, QuestReward.class)
                    .newInstance(id, name, descriptions, missions, next, reward)
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseMissionFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            String type = yaml.getString(key + ".type");
            ConfigurationSection detail = yaml.getConfigurationSection(key + ".detail");
            List<String> navigationEvents = yaml.getStringList(key + ".navigation-events");
            List<String> completeEvents = yaml.getStringList(key + ".complete-events");
            List<String> descriptions = yaml.getStringList(key + ".descriptions");

            try {
                Class<? extends AbstractMission> preset = this.missionPreset.get(type);
                if (preset == null) continue;

                this.missions.put(key, preset
                        .getConstructor(String.class, String.class, List.class, List.class, List.class, ConfigurationSection.class)
                        .newInstance(key, type, navigationEvents, completeEvents, descriptions, detail)
                );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void parseConversationFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            ConfigurationSection section = yaml.getConfigurationSection(key);
            this.conversations.put(key, this.parseConversation(key, section));
        }
    }

    private Conversation parseConversation(String id, ConfigurationSection section) {
        String npc = section.getString("__npc__");
        String complete = section.getString("__complete__");
        Map<String, Dialogue> dialogues = new LinkedHashMap<>();

        for (String key : section.getKeys(false)) {
            if (key.startsWith("__")) continue;
            List<String> response = section.getStringList(key + ".response");
            List<String> events = section.getStringList(key + ".events");
            Map<String, ReplayOption> options = new LinkedHashMap<>();

            ConfigurationSection reply = section.getConfigurationSection(key + ".reply");
            for (String elm : reply.getKeys(false)) {
                String text = reply.getString(elm + ".text");
                List<String> replyEvents = reply.getStringList(elm + ".events");
                String go = reply.getString(elm + ".go");
                options.put(elm, new ReplayOption(elm, text, replyEvents, go));
            }

            dialogues.put(key, new Dialogue(key, response, events, options));
        }

        return new Conversation(id, npc, complete, dialogues);
    }
    
    private void parseEventFile(File file) {
        if (!file.exists()) return;
        
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            String type = yaml.getString(key + ".type");
            ConfigurationSection section = yaml.getConfigurationSection(key + ".detail");
            
            try {
                Class<? extends AbstractEvent> preset = this.eventPreset.get(type);
                if (preset == null) continue;
                
                this.events.put(key, preset
                        .getConstructor(String.class, ConfigurationSection.class)
                        .newInstance(key, section)
                );
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadChain() {
        this.loadChainRequire();
        this.loadChainReward();
    }

    private void loadChainRequire() {
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

            this.chainRequire.put(key, new ChainRequire(item, pair, dungeon, mobs, scope));
        }
    }

    private void loadChainReward() {
        File file = new File(plugin.getDataFolder(), "chain/reward.yml");
        if (!file.exists()) return;

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        for (String key : yaml.getKeys(false)) {
            int money = yaml.getInt(key + ".money");
            List<String> data = yaml.getStringList(key + ".items");

            this.chainReward.put(Integer.parseInt(key), new ChainReward(money, ChainReward.parse(data)));
        }
    }
}
