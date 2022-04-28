package net.sakuragame.eternal.justquest.core.mission.progress;

import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;

import java.util.UUID;

public class EmptyProgress extends AbstractProgress {

    public EmptyProgress(UUID uuid, String questID) {
        super(uuid, questID);
    }

    @Override
    public void push() {
    }

    @Override
    public void push(String key) {
    }

    @Override
    public void push(int i) {}

    @Override
    public void push(String key, int i) {}

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public String getConvertData() {
        return "";
    }
}
