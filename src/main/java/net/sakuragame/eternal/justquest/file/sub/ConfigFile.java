package net.sakuragame.eternal.justquest.file.sub;

import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.justquest.JustQuest;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class ConfigFile {
    private static YamlConfiguration yaml;

    public static String prefix;
    public static List<String> defaultAllot;

    public static void init() {
        yaml = JustQuest.getFileManager().getConfig();

        prefix = getString("prefix");
        defaultAllot = yaml.getStringList("default-allot");
    }

    private static String getString(String path) {
        return MegumiUtil.onReplace(yaml.getString(path));
    }

    private static List<String> getStringList(String path) {
        return MegumiUtil.onReplace(yaml.getStringList(path));
    }
}
