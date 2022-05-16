package net.sakuragame.eternal.justquest.core.conversation.io;

import org.bukkit.entity.Player;

public interface IConversationIO {

    void start();

    void display();

    void nextDialogue(Player player, String key);

    void end();
}
