package net.sakuragame.eternal.justquest.core.quest.sub;

import com.taylorswiftcn.justwei.util.MegumiUtil;
import net.sakuragame.eternal.gemseconomy.api.GemsEconomyAPI;
import net.sakuragame.eternal.justmessage.JustMessage;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.QuestEvent;
import net.sakuragame.eternal.justquest.core.ChainManager;
import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.mission.IMission;
import net.sakuragame.eternal.justquest.core.quest.AbstractQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.file.sub.ChainFile;
import net.sakuragame.eternal.justquest.util.Scheduler;
import net.sakuragame.eternal.justquest.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class ChainQuest extends AbstractQuest {

    public ChainQuest(String ID, String name, List<String> descriptions) {
        super(ID, name, false, descriptions, Collections.singletonList(ChainManager.MISSION_ID), null, null);
    }

    @Override
    public void allot(UUID uuid) {
        IMission mission = JustQuest.getProfileManager().getMission(ChainManager.MISSION_ID);
        if (mission == null) return;

        mission.active(uuid, this.getID());

        QuestEvent.Allot event = new QuestEvent.Allot(Bukkit.getPlayer(uuid), this);
        event.call();
    }

    @Override
    public void completed(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        int chain = account.getChain() + 1;

        if (chain % 20 == 0) {
            JustMessage.getChatManager().sendAll("⒞ §a恭喜§7" + player.getName() + "§a完成 §b" + chain + "§a 次跑环,获得了大量奖励!");
        }

        player.sendTitle("§a§l商行任务已完成", "§3§l今日跑环进度(" + chain + "/60)", 10, 20, 10);
    }

    @Override
    public void award(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        QuestProgress progress = account.getProgresses().get(this.getID());
        if (progress == null) return;
        if (!progress.isCompleted()) return;

        IMission mission = JustQuest.getProfileManager().getMission(progress.getMissionID());
        mission.restrain(uuid);

        int chain = account.getChain();

        int money = ChainFile.getRewardMoney(chain + 1);
        Map<String, Integer> items = ChainFile.getRewardItems(chain + 1);

        GemsEconomyAPI.deposit(uuid, money);
        Player player = Bukkit.getPlayer(uuid);
        Utils.giveItems(player, items);

        account.deleteQuestProgress(this.getID());
        account.updateChain(chain + 1);

        QuestEvent.Finished event = new QuestEvent.Finished(Bukkit.getPlayer(uuid), this);
        event.call();

        JustQuest.getChainManager().allotQuest(player);
    }

    @Override
    public String getName(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        int chain = account.getChain();
        return this.getName().replace("%current%", (chain + 1) + "");
    }

    @Override
    public QuestType getType() {
        return QuestType.CQ;
    }

    @Override
    public String getRewardDesc(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        int chain = account.getChain();

        return "&a金币: &f" + ChainFile.getRewardMoney(chain + 1);
    }

    @Override
    public Map<String, Integer> getRewardItems(UUID uuid) {
        QuestAccount account = JustQuest.getAccountManager().getAccount(uuid);
        int chain = account.getChain();

        return ChainFile.getRewardItems(chain + 1);
    }

    @Override
    public boolean isAllowCancel() {
        return true;
    }

    @Override
    public long getExpireTime() {
        return Utils.getNextDayZero();
    }
}
