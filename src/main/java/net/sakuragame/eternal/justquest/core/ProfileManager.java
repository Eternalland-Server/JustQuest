package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.conversation.Dialogue;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.ReplayOption;
import net.sakuragame.eternal.justquest.core.data.NPCConfig;
import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.mission.AbstractMission;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.mission.sub.ConversationMission;
import net.sakuragame.eternal.justquest.core.mission.sub.CreateTeamMission;
import net.sakuragame.eternal.justquest.core.mission.sub.LearnAbilityMission;
import net.sakuragame.eternal.justquest.core.mission.sub.MobKillerMission;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.quest.QuestReward;
import net.sakuragame.eternal.justquest.core.quest.sub.MainQuest;
import net.sakuragame.eternal.justquest.core.quest.sub.SIdeQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ProfileManager {

    private Map<QuestType, Class<? extends AbstractQuest>> questPreset;
    private Map<String, Class<? extends AbstractMission>> missionPreset;

    private Map<Integer, NPCConfig> npcConfig;
    private Map<String, AbstractQuest> quests;
    private Map<String, AbstractMission> missions;
    private Map<String, Conversation> conversations;

    public void init() {
        this.questPreset = new HashMap<>();
        this.missionPreset = new HashMap<>();

        this.npcConfig = new HashMap<>();
        this.quests = new HashMap<>();
        this.missions = new HashMap<>();
        this.conversations = new HashMap<>();

        this.registerQuestPreset();
        this.registerMissionPreset();

        this.loadNPC();
        this.loadProfile();
    }

    public NPCConfig getNPCConfig(int ID) {
        return this.npcConfig.get(ID);
    }

    public IQuest getQuest(String key) {
        return this.quests.get(key);
    }

    public IMission getMission(String key) {
        return this.missions.get(key);
    }

    public Conversation getConversation(String key) {
        return this.conversations.get(key);
    }

    public List<IMission> getMissions() {
        return new ArrayList<>(this.missions.values());
    }

    private void registerQuestPreset() {
        this.registerQuestPreset(QuestType.MQ, MainQuest.class);
        this.registerQuestPreset(QuestType.SQ, SIdeQuest.class);
    }

    private void registerMissionPreset() {
        this.registerMissionPreset("conversation", ConversationMission.class);
        this.registerMissionPreset("mob_killer", MobKillerMission.class);
        this.registerMissionPreset("learn_ability", LearnAbilityMission.class);
        this.registerMissionPreset("create_team", CreateTeamMission.class);
    }

    public void registerQuestPreset(QuestType type, Class<? extends AbstractQuest> questPreset) {
        this.questPreset.put(type, questPreset);
    }

    public void registerMissionPreset(String key, Class<? extends AbstractMission> missionPreset) {
        this.missionPreset.put(key, missionPreset);
    }

    private void loadNPC() {
        File dir = new File(JustQuest.getInstance().getDataFolder(), "npc");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).filter(k -> k.getName().endsWith(".yml")).forEach(this::parseNPCConfigFile);
    }

    private void loadProfile() {
        File dir = new File(JustQuest.getInstance().getDataFolder(), "profile");
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) return;

        Arrays.stream(files).filter(File::isDirectory).forEach(sub -> {
            this.parseQuestFile(new File(sub, "quest.yml"));
            this.parseMissionFile(new File(sub, "missions.yml"));
            this.parseConversationFile(new File(sub, "conversations.yml"));
        });
    }

    private void parseNPCConfigFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        int id = yaml.getInt("id");
        String name = yaml.getString("name");
        double scale = yaml.getDouble("scale", 2);
        List<String> questConversation = yaml.getStringList("quest-conversation");
        Conversation defaultConversation = this.parseConversation("_", yaml.getConfigurationSection("default-conversation"));

        this.npcConfig.put(id, new NPCConfig(id, name, scale, questConversation, defaultConversation));
    }

    private void parseQuestFile(File file) {
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        String id = yaml.getString("id");
        String name = yaml.getString("name");
        QuestType type = QuestType.valueOf(yaml.getString("type").toUpperCase());
        List<String> missions = yaml.getStringList("missions");

        double exp = yaml.getDouble("award.exp");
        double money = yaml.getDouble("award.money");
        int coins = yaml.getInt("award.coins");
        List<String> items = yaml.getStringList("award.items");
        Map<String, Integer> map = new HashMap<>();
        items.forEach(s -> {
            String[] part = s.split(" ", 2);
            if (part.length > 2) {
                map.put(part[0], Integer.parseInt(part[1]));
            }
        });

        QuestReward reward = new QuestReward(exp, money, coins, map);

        try {
            Class<? extends AbstractQuest> preset = this.questPreset.get(type);
            if (preset == null) return;

            this.quests.put(id, preset
                    .getConstructor(String.class, String.class, List.class, QuestReward.class)
                    .newInstance(id, name, missions, reward)
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
            List<String> descriptions = yaml.getStringList(key + ".descriptions");

            try {
                Class<? extends AbstractMission> preset = this.missionPreset.get(type);
                if (preset == null) continue;

                this.missions.put(key, preset
                        .getConstructor(String.class, String.class, List.class, ConfigurationSection.class)
                        .newInstance(key, type, descriptions, detail)
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
        Map<String, Dialogue> dialogues = new LinkedHashMap<>();

        for (String key : section.getKeys(false)) {
            List<String> response = section.getStringList(key + ".response");
            Map<String, ReplayOption> options = new LinkedHashMap<>();
            ConfigurationSection reply = section.getConfigurationSection(key + ".reply");
            for (String elm : reply.getKeys(false)) {
                String text = reply.getString(elm + ".text");
                String go = reply.getString(elm + ".go");
                options.put(elm, new ReplayOption(elm, text, go));
            }

            dialogues.put(key, new Dialogue(key, response, options));
        }

        return new Conversation(id, dialogues);
    }
}
