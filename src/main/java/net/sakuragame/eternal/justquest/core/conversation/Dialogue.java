package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class Dialogue {

    private final String ID;

    private final List<String> response;
    private final Map<String, ReplayOption> options;

    public Dialogue(String ID, List<String> response, Map<String, ReplayOption> options) {
        this.ID = ID;
        this.response = response;
        this.options = options;
    }

    public ReplayOption getOption(String key) {
        return this.options.get(key);
    }
}
