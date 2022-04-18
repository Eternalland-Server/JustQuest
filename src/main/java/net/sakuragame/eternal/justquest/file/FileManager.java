package net.sakuragame.eternal.justquest.file;

import com.taylorswiftcn.justwei.file.JustConfiguration;
import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import net.sakuragame.eternal.justquest.file.sub.MessageFile;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;


public class FileManager extends JustConfiguration {

    private final JustQuest plugin;
    @Getter private YamlConfiguration config;
    @Getter private YamlConfiguration message;

    public FileManager(JustQuest plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public void init() {
        config = initFile("config.yml");
        message = initFile("message.yml");

        ConfigFile.init();
        MessageFile.init();

        this.initProfile();
    }

    private void initProfile() {
        File profile = new File(plugin.getDataFolder(), "profile");
        if (profile.mkdirs()) {
            File dir = new File(profile, "template");
            if (dir.mkdirs()) {
                this.copy(profile, "template/quest.yml");
                this.copy(profile, "template/missions.yml");
                this.copy(profile, "template/conversations.yml");
            }
        }

        File npcConfig = new File(plugin.getDataFolder(), "npc");
        if (npcConfig.mkdirs()) {
            this.copy(plugin.getDataFolder(), "npc/npc.yml");
        }
    }

    private void copy(File dir, String file) {
        MegumiUtil.copyFile(plugin.getResource(file), new File(dir, file));
    }
}
