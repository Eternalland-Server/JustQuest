package net.sakuragame.eternal.justquest.core.data;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResult {

    private final int current;
    private final int total;
    private final List<String> list;

    public PageResult(int current, int total, List<String> list) {
        this.current = current;
        this.total = total;
        this.list = list;
    }
}
