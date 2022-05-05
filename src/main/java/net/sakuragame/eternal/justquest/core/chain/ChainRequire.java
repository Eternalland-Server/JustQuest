package net.sakuragame.eternal.justquest.core.chain;

import net.sakuragame.eternal.dragoncore.util.Pair;

public class ChainRequire {

    private final String ID;
    private final Pair<Integer, Integer> amount;
    private final String dungeon;
    private final String mobs;
    private final int scope;

    public ChainRequire(String ID, Pair<Integer, Integer> amount, String dungeon, String mobs, int scope) {
        this.ID = ID;
        this.amount = amount;
        this.dungeon = dungeon;
        this.mobs = mobs;
        this.scope = scope;
    }
}
