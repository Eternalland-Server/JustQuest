package net.sakuragame.eternal.justquest.commands;

import lombok.Getter;

public enum CommandPerms {

    USER("justquest.user"),
    ADMIN("justquest.admin");

    @Getter
    private final String node;

    CommandPerms(String node) {
        this.node = node;
    }
}