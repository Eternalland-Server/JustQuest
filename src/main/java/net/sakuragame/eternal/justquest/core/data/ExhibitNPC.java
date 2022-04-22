package net.sakuragame.eternal.justquest.core.data;

import lombok.Getter;

import java.util.List;

@Getter
public class ExhibitNPC {

    private final String ID;
    private final String name;
    private final List<String> descriptions;
    private final List<String> clothes;

    public ExhibitNPC(String ID, String name, List<String> descriptions, List<String> clothes) {
        this.ID = ID;
        this.name = name;
        this.descriptions = descriptions;
        this.clothes = clothes;
    }
}
