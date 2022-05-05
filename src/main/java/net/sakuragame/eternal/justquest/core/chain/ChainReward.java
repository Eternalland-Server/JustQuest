package net.sakuragame.eternal.justquest.core.chain;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ChainReward {

    private final int money;
    private final List<Item> items;

    public ChainReward(int money, List<Item> items) {
        this.money = money;
        this.items = items;
    }

    public static List<Item> parse(List<String> data) {
        List<Item> items = new ArrayList<>();
        for (String s : data) {
            String[] args = s.split(" ", 3);
            if (args.length < 3) continue;
            items.add(new Item(args[0], Integer.parseInt(args[1]), Double.parseDouble(args[2])));
        }

        return items;
    }

    @Getter
    private static class Item {

        private final String ID;
        private final int amount;
        private final double probability;

        public Item(String ID, int amount, double probability) {
            this.ID = ID;
            this.amount = amount;
            this.probability = probability;
        }
    }
}
