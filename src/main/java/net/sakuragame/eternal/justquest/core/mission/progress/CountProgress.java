package net.sakuragame.eternal.justquest.core.mission.progress;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;

import java.util.UUID;

@Getter
public class CountProgress extends AbstractProgress {

    private int count;

    public CountProgress(UUID uuid, String questID, int count) {
        super(uuid, questID);
        this.count = count;
    }

    public CountProgress(UUID uuid, String questID, String data) {
        super(uuid, questID);
        this.count = Integer.parseInt(data);
    }

    @Override
    public void push() {
        this.count--;
    }

    @Override
    public void push(String key) {
        this.push();
    }

    @Override
    public void push(int i) {
        this.count -= i;
    }

    @Override
    public void push(String key, int i) {
        this.push(i);
    }

    @Override
    public boolean isFinished() {
        return this.count <= 0;
    }

    @Override
    public String getConvertData() {
        return "" + count;
    }
}
