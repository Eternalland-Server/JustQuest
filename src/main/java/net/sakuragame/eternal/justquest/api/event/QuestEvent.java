package net.sakuragame.eternal.justquest.api.event;

import lombok.Getter;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import org.bukkit.entity.Player;

@Getter
public abstract class QuestEvent extends JustEvent {

    private final IQuest quest;

    public QuestEvent(Player who, IQuest quest) {
        super(who);
        this.quest = quest;
    }

    public static class Allot extends QuestEvent {
        public Allot(Player who, IQuest quest) {
            super(who, quest);
        }
    }

    public static class Completed extends QuestEvent {
        public Completed(Player who, IQuest quest) {
            super(who, quest);
        }
    }

    public static class Cancel extends QuestEvent {
        public Cancel(Player who, IQuest quest) {
            super(who, quest);
        }
    }

    public static class Finished extends QuestEvent {
        public Finished(Player who, IQuest quest) {
            super(who, quest);
        }
    }
}
