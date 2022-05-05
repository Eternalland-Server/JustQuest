package net.sakuragame.eternal.justquest.core;

import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountManager {

    private final Map<UUID, QuestAccount> accounts;

    public AccountManager() {
        this.accounts = new HashMap<>();
    }

    public void loadAccount(UUID uuid) {
        QuestAccount account = JustQuest.getStorageManager().getAccount(uuid);
        this.accounts.put(uuid, account);

        account.resumeQuestsProgress();
    }

    public QuestAccount getAccount(Player player) {
        return this.getAccount(player.getUniqueId());
    }

    public QuestAccount getAccount(UUID uuid) {
        return this.accounts.get(uuid);
    }

    public void removeAccount(Player player) {
        this.removeAccount(player.getUniqueId());
    }

    public void removeAccount(UUID uuid) {
        QuestAccount account = this.accounts.get(uuid);
        account.saveQuestsProgress();

        this.accounts.remove(uuid);
    }

    public void saveAccountsQuest() {
        this.accounts.values().forEach(QuestAccount::saveQuestsProgress);
    }

    public void resumeAccountsQuest() {
        this.accounts.values().forEach(QuestAccount::resumeQuestsProgress);
    }
}
