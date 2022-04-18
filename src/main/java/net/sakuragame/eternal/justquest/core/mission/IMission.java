package net.sakuragame.eternal.justquest.core.mission;

import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;

import java.util.List;
import java.util.UUID;

public interface IMission {

    String getID();

    List<String> getDescriptions();

    /**
     * 启用
     */
    void enable();

    /**
     * 禁用
     */
    void disable();

    /**
     * 为玩家激活目标任务
     *
     * @param uuid    uuid
     * @param questID 任务id
     */
    void active(UUID uuid, String questID);

    /**
     * 移除玩家目标任务
     *
     * @param uuid uuid
     */
    void restrain(UUID uuid);

    /**
     * 为玩家继续激活目标任务
     *
     * @param uuid    uuid
     * @param questID 任务id
     * @param data    进度数据
     */
    void keep(UUID uuid, String questID, String data);

    /**
     * 完成任务
     *
     * @param uuid uuid
     */
    void complete(UUID uuid);

    /**
     * 构建任务目标显示信息
     *
     * @param uuid uuid
     * @return {@link ScreenUI}
     */
    ScreenUI getProgressDisplay(UUID uuid);

    IProgress newProgress(UUID uuid, String questID);

    IProgress newProgress(UUID uuid, String questID, String data);
}
