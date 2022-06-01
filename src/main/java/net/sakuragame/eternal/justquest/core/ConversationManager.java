package net.sakuragame.eternal.justquest.core;

import eos.moe.armourers.api.DragonAPI;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.io.ExhibitConversationIO;
import net.sakuragame.eternal.justquest.core.conversation.io.UIConversationIO;
import net.sakuragame.eternal.justquest.core.data.ExhibitNPC;
import net.sakuragame.eternal.justquest.core.data.NPCConfig;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConversationManager {

    private final Map<UUID, String> cache;
    private final Map<UUID, Integer> tryTaskID;

    public ConversationManager() {
        this.cache = new HashMap<>();
        this.tryTaskID = new HashMap<>();
    }

    public void enter(Player player, String id) {
        NPCConfig config = JustQuest.getProfileManager().getNPCConfig(id);
        if (config == null) {
            ExhibitNPC exhibit = JustQuest.getProfileManager().getExhibitNPC(id);
            if (exhibit == null) return;
            new ExhibitConversationIO(player, exhibit);
            return;
        }

        QuestAccount account = JustQuest.getAccountManager().getAccount(player);

        Conversation def = config.getDefaultConversation();
        for (String quest : config.getQuestConversation()) {
            if (account.getProgresses().containsKey(quest)) {
                QuestProgress progress = account.getProgresses().get(quest);
                if (progress.isCompleted()) continue;

                String mission = account.getProgresses().get(quest).getMissionID();
                Conversation conv = JustQuest.getProfileManager().getConversation(mission);
                if (conv == null) continue;
                if (conv.getNPC() != null && !conv.getNPC().equals(id)) continue;

                def = conv;
                break;
            }
        }

        if (def == null) return;

        ConversationEvent.Enter event = new ConversationEvent.Enter(player, id, def);
        event.call();
        if (event.isCancelled()) return;

        new UIConversationIO(player, id, def);
        this.addCache(player.getUniqueId(), id);
    }

    public void tryClothes(Player player, List<String> clothes) {
        UUID uuid = player.getUniqueId();
        if (this.tryTaskID.containsKey(uuid)) {
            Bukkit.getScheduler().cancelTask(this.tryTaskID.remove(uuid));
        }

        DragonAPI.setEntitySkin(uuid, clothes);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                DragonAPI.updatePlayerSkin(player);
            }
        }.runTaskLaterAsynchronously(JustQuest.getInstance(), 400);
        this.tryTaskID.put(uuid, task.getTaskId());
    }

    public void cancel(UUID uuid) {
        if (!this.tryTaskID.containsKey(uuid)) return;
        Bukkit.getScheduler().cancelTask(this.tryTaskID.remove(uuid));
    }

    public void addCache(UUID uuid, String npcID) {
        this.cache.put(uuid, npcID);
    }

    public void removeCache(UUID uuid) {
        this.cache.remove(uuid);
    }

    public String getCache(UUID uuid) {
        return this.cache.get(uuid);
    }
}
