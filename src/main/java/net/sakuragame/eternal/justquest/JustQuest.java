package net.sakuragame.eternal.justquest;

import net.sakuragame.eternal.justquest.commands.MainCommand;
import net.sakuragame.eternal.justquest.core.*;
import net.sakuragame.eternal.justquest.file.FileManager;
import lombok.Getter;
import net.sakuragame.eternal.justquest.listener.*;
import net.sakuragame.eternal.justquest.storage.StorageManager;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class JustQuest extends JavaPlugin {
    @Getter private static JustQuest instance;

    @Getter private static FileManager fileManager;

    @Getter private static AccountManager accountManager;

    @Getter private static ProfileManager profileManager;
    @Getter private static ChainManager chainManager;
    @Getter private static QuestManager questManager;
    @Getter private static ConversationManager conversationManager;

    @Getter private static StorageManager storageManager;

    @Getter private static QuestUIManager uiManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        instance = this;

        fileManager = new FileManager(this);
        fileManager.init();

        profileManager = new ProfileManager(this);
        profileManager.init();

        chainManager = new ChainManager();
        chainManager.init();

        questManager = new QuestManager();
        accountManager = new AccountManager();
        conversationManager = new ConversationManager();
        storageManager = new StorageManager();
        uiManager = new QuestUIManager();

        this.registerListener(new PlayerListener());
        this.registerListener(new NPCListener());
        this.registerListener(new ConversationListener());
        this.registerListener(new QuestListener());
        this.registerListener(new UIListener());
        this.registerListener(new ChainListener());
        getCommand("jquest").setExecutor(new MainCommand());

        long end = System.currentTimeMillis();

        getLogger().info("加载成功! 用时 %time% ms".replace("%time%", String.valueOf(end - start)));
    }

    @Override
    public void onDisable() {
        getLogger().info("卸载成功!");
    }

    public String getVersion() {
        String packet = Bukkit.getServer().getClass().getPackage().getName();
        return packet.substring(packet.lastIndexOf('.') + 1);
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void reload() {
        accountManager.saveAccountsQuest();
        fileManager.init();
        profileManager.init();
        chainManager.init();
        accountManager.resumeAccountsQuest();
    }
}
