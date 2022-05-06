package net.sakuragame.eternal.justquest.core.mission;

import lombok.Getter;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;

@Getter
public abstract class AbstractMission implements IMission, Listener {

    private final String ID;

    private final String type;
    private final List<String> navigationEvents;
    private final List<String> completeEvents;
    private final List<String> descriptions;

    private final Map<UUID, String> members;

    public AbstractMission(String ID, String type, List<String> navigationEvents, List<String> completeEvents, List<String> descriptions, ConfigurationSection section) {
        this.ID = ID;
        this.type = type;
        this.navigationEvents = navigationEvents;
        this.completeEvents = completeEvents;
        this.descriptions = descriptions;

        this.members = new HashMap<>();
    }

    @Override
    public List<String> getDescriptions(UUID uuid) {
        return this.descriptions;
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
        this.members.put(uuid, questID);

        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        IProgress progress = this.newProgress(uuid, questID);
        account.newQuestProgress(questID, this.ID, progress);
        if (account.getTrace() == null) {
            account.setQuestTrace(questID);
            account.updateTraceBar();
        }
        progress.update();

        if (this.members.size() == 1) this.enable();
    }

    @Override
    public void restrain(UUID uuid) {
        this.members.remove(uuid);
        if (this.members.size() == 0) this.disable();
    }

    @Override
    public void abandon(UUID uuid) {
        this.members.remove(uuid);
        if (this.members.size() == 0) this.disable();
    }

    @Override
    public void keep(UUID uuid, String questID) {
        this.members.put(uuid, questID);
        if (this.members.size() == 1) this.enable();
    }

    @Override
    public void complete(UUID uuid) {
        String questID = this.members.get(uuid);
        JustQuest.getQuestManager().fireEvents(uuid, this.completeEvents);

        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        account.completeMission(questID, this.ID);
    }

    @Override
    public void navigation(Player player) {
        if (this.navigationEvents.isEmpty()) return;

        JustQuest.getQuestManager().fireEvents(player.getUniqueId(), this.navigationEvents);
    }

    public IProgress getData(UUID uuid) {
        String questID = this.members.get(uuid);
        if (questID == null) return null;

        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        return account.getProgresses().get(questID).getProgress();
    }
}
