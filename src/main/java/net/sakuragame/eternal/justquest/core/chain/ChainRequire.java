package net.sakuragame.eternal.justquest.core.chain;

import lombok.Getter;
import net.sakuragame.eternal.dragoncore.util.Pair;

import java.util.Random;

@Getter
public class ChainRequire {

    private final String ID;
    private final String item;
    private final Pair<Integer, Integer> amount;
    private final String dungeon;
    private final String mobs;
    private final int scope;

    public ChainRequire(String ID, String item, Pair<Integer, Integer> amount, String dungeon, String mobs, int scope) {
        this.ID = ID;
        this.item = item;
        this.amount = amount;
        this.dungeon = dungeon;
        this.mobs = mobs;
        this.scope = scope;
    }

    public int getRandomAmount() {
        Random random = new Random();
        return amount.getKey() + random.nextInt(amount.getValue() - amount.getKey());
    }
}
