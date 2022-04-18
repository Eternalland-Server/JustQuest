package net.sakuragame.eternal.justquest.core.quest;

import net.sakuragame.eternal.justquest.core.data.QuestType;

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
     * @param data      任务数据
     */
    void resume(UUID uuid, String missionID, String data);

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
     * 获取下一个子任务
     *
     * @param id 当前子任务ID
     * @return {@link String}
     */
    String nextMission(String id);
}
