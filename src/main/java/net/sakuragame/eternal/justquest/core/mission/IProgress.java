package net.sakuragame.eternal.justquest.core.mission;

import java.util.UUID;

public interface IProgress {

    UUID getUUID();

    String getQuestID();

    void push(String key);

    void push(String key, int i);

    boolean isFinished();

    void update();

    String getConvertData();
}
