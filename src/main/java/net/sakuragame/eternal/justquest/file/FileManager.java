package net.sakuragame.eternal.justquest.file;

import com.taylorswiftcn.justwei.file.JustConfiguration;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.file.sub.ChainFile;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


public class FileManager extends JustConfiguration {

    private final JustQuest plugin;
    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration chain;

    public FileManager(JustQuest plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void init() {
        config = initFile("config.yml");
        chain = initFile("chain.yml");

        ConfigFile.init();
        ChainFile.init();

        this.initDefaultConfig();
        this.initChainConfig();
    }

    private void initDefaultConfig() {
        File profile = new File(plugin.getDataFolder(), "quest");
        if (profile.mkdirs()) {
            File dir = new File(profile, "template");
            if (dir.mkdirs()) {
                this.copy(profile, "template/quest.yml");
                this.copy(profile, "template/missions.yml");
                this.copy(profile, "template/conversations.yml");
                this.copy(profile, "template/events.yml");
            }
        }

        File npcConfig = new File(plugin.getDataFolder(), "npc");
        if (npcConfig.mkdirs()) {
            this.copy(plugin.getDataFolder(), "npc/npc.yml");
        }

        File eventConfig = new File(plugin.getDataFolder(), "event");
        if (eventConfig.mkdirs()) {
            this.copy(plugin.getDataFolder(), "event/events.yml");
        }

        File conditionConfig = new File(plugin.getDataFolder(), "condition");
        if (conditionConfig.mkdirs()) {
            this.copy(plugin.getDataFolder(), "condition/conditions.yml");
        }
    }

    private void initChainConfig() {
        File dir = new File(plugin.getDataFolder(), "chain");
        if (!dir.mkdirs()) return;
        MegumiUtil.copyFile(plugin.getResource("chain/conversations.yml"), new File(dir, "conversations.yml"));
        MegumiUtil.copyFile(plugin.getResource("chain/requirement.yml"), new File(dir, "requirement.yml"));
    }

    private void copy(File dir, String file) {
        MegumiUtil.copyFile(plugin.getResource(file), new File(dir, file));
    }
}
