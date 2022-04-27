package net.sakuragame.eternal.justquest.core.mission;

import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IMission {

    String getID();

    List<String> getDescriptions();

    String getPlugin();

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
     * 移除并保存任务
     *
     * @param uuid uuid
     */
    void restrain(UUID uuid);

    /**
     * 放弃任务
     *
     * @param uuid uuid
     */
    void abandon(UUID uuid);

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
     * 开启任务导航
     *
     * @param player 玩家
     */
    void navigation(Player player);

    /**
     * 构建任务目标显示信息
     *
     * @param uuid uuid
     * @return {@link ScreenUI}
     */
    ScreenUI getProgressDisplay(UUID uuid);

    ScreenUI getCompleteDisplay(UUID uuid);

    IProgress newProgress(UUID uuid, String questID);

    IProgress newProgress(UUID uuid, String questID, String data);
}
