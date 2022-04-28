package net.sakuragame.eternal.justquest.core.mission.progress;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import net.sakuragame.eternal.justquest.core.mission.AbstractProgress;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ExpendProgress extends AbstractProgress {

    private final Map<String, Integer> map;

    public ExpendProgress(UUID uuid, String questID, Map<String, Integer> map) {
        super(uuid, questID);
        this.map = map;
    }

    public ExpendProgress(UUID uuid, String questID, String data) {
        super(uuid, questID);
        this.map = JSON.parseObject(data, new TypeReference<LinkedHashMap<String, Integer>>() {});
    }

    public int getCount(String key) {
        return this.map.get(key);
    }

    @Override
    public void push() {}

    @Override
    public void push(String key) {
        this.map.computeIfPresent(key, (k, v) -> Math.max(0, v - 1));
    }

    @Override
    public void push(int i) {}

    @Override
    public void push(String key, int i) {
        this.map.computeIfPresent(key, (k, v) -> Math.max(0, v - i));
    }

    @Override
    public boolean isFinished() {
        for (int count : this.map.values()) {
            if (count > 0) return false;
        }

        return true;
    }

    @Override
    public String getConvertData() {
        return JSON.toJSONString(map);
    }
}
