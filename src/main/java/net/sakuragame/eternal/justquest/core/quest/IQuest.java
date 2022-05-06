package net.sakuragame.eternal.justquest.core.quest;

import net.sakuragame.eternal.justquest.core.data.QuestType;
import net.sakuragame.eternal.justquest.core.mission.IMission;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IQuest {

    /**
     * 获取任务ID
     *
     * @return {@link String}
     */
    String getID();

    /**
     * 获取任务名称
     *
     * @return {@link String}
     */
    String getName();

    /**
     * 获取任务类型
     *
     * @return {@link QuestType}
     */
    QuestType getType();

    /**
     * 获取任务描述
     *
     * @return {@link List}<{@link String}>
     */
    List<String> getDescriptions();

    String getRewardDesc(UUID uuid);

    Map<String, Integer> getRewardItems(UUID uuid);

    /**
     * 分配任务给玩家
     *
     * @param uuid uuid
     */
    void allot(UUID uuid);

    /**
     * 恢复玩家任务
     *
     * @param uuid      uuid
     * @param missionID 子任务id
     */
    void resume(UUID uuid, String missionID);

    /**
     * 取消玩家任务
     *
     * @param uuid uuid
     */
    void cancel(UUID uuid);

    /**
     * 是否允许取消任务
     *
     * @return boolean
     */
    boolean isAllowCancel();

    /**
     * 发放任务奖励
     *
     * @param uuid uuid
     */
    void award(UUID uuid);

    /**
     * 获取后续任务
     *
     * @return {@link String}
     */
    String getNextQuest();

    /**
     * 获取下一个子任务
     *
     * @param id 当前子任务ID
     * @return {@link String}
     */
    IMission getNextMission(String id);

    long getExpireTime();
}
