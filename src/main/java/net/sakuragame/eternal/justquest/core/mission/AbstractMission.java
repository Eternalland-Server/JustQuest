package net.sakuragame.eternal.justquest.core.mission;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public abstract class AbstractMission implements IMission, Listener {

    private final String ID;

    private final String type;
    private final List<String> events;
    private final List<String> descriptions;

    private final Map<UUID, IProgress> data;

    public AbstractMission(String ID, String type, List<String> events, List<String> descriptions, ConfigurationSection section) {
        this.ID = ID;
        this.type = type;
        this.events = events;
        this.descriptions = descriptions;

        this.data = new HashMap<>();
    }

    @Override
    public String getPlugin() {
        return null;
    }

    private boolean isInvalid() {
        return this.getPlugin() != null && Bukkit.getPluginManager().getPlugin(this.getPlugin()) == null;
    }

    @Override
    public void enable() {
        if (this.isInvalid()) return;
        Bukkit.getPluginManager().registerEvents(this, JustQuest.getInstance());
    }

    @Override
    public void disable() {
        if (this.isInvalid()) return;
        HandlerList.unregisterAll(this);
    }

    @Override
    public void active(UUID uuid, String questID) {
        IProgress progress = this.newProgress(uuid, questID);
        this.data.put(uuid, progress);

        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        account.saveQuestProgress(questID, this.ID, progress.getConvertData());

        if (account.getQuestTrace() == null) {
            account.setQuestTrace(questID);
            account.updateTraceBar();
        }

        progress.update();
        if (this.data.size() == 1) this.enable();
    }

    @Override
    public void restrain(UUID uuid) {
        IProgress progress = this.data.remove(uuid);
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        account.saveQuestProgress(progress.getQuestID(), this.ID, progress.getConvertData());

        if (this.data.size() == 0) this.disable();
    }

    @Override
    public void abandon(UUID uuid) {
        this.data.remove(uuid);
        if (this.data.size() == 0) this.disable();
    }

    @Override
    public void keep(UUID uuid, String questID, String data) {
        IProgress progress = this.newProgress(uuid, questID, data);
        this.data.put(uuid, progress);

        if (this.data.size() == 1) this.enable();
    }

    @Override
    public void complete(UUID uuid) {
        IProgress progress = this.data.remove(uuid);
        JustQuest.getQuestManager().fireEvents(uuid, this.events);

        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        account.completeMission(progress.getQuestID(), this.ID);
    }

    @Override
    public void navigation(Player player) {

    }

    public IProgress getData(UUID uuid) {
        return this.data.get(uuid);
    }
}
