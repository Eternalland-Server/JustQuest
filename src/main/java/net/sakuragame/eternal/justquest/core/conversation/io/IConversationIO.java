package net.sakuragame.eternal.justquest.core.conversation.io;

public interface IConversationIO {

    void start();

    void display();

    void setDialogue(String key);

    void end();
}
