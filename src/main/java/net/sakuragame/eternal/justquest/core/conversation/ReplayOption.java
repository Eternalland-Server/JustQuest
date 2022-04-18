package net.sakuragame.eternal.justquest.core.conversation;

import lombok.Getter;

@Getter
public class ReplayOption {

    private final String ID;

    private final String text;
    private final String go;

    public ReplayOption(String ID, String text, String go) {
        this.ID = ID;
        this.text = text;
        this.go = go;
    }
}
