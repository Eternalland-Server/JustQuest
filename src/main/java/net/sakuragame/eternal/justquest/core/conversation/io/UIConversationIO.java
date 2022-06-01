package net.sakuragame.eternal.justquest.core.conversation.io;

import com.taylorswiftcn.megumi.uifactory.event.comp.UIFCompSubmitEvent;
import com.taylorswiftcn.megumi.uifactory.event.screen.UIFScreenCloseEvent;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.api.event.ConversationEvent;
import net.sakuragame.eternal.justquest.core.conversation.Conversation;
import net.sakuragame.eternal.justquest.core.conversation.Dialogue;
import net.sakuragame.eternal.justquest.core.conversation.ReplayOption;
import net.sakuragame.eternal.justquest.ui.OperateCode;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class UIConversationIO implements IConversationIO, Listener {

    private final Player player;
    private final String npcID;

    private final Conversation conversation;

    private final String npcName;

    private Dialogue dialogue;
    private String lastChoiceOption;

    private boolean opened;
    private boolean switching;

    public UIConversationIO(Player player, String npcID, Conversation conversation) {
        this(player, npcID, conversation, null);
    }

    public UIConversationIO(Player player, String npcID, Conversation conversation, String npcName) {
        this.player = player;
        this.npcID = npcID;
        this.conversation = conversation;

        this.npcName = npcName == null ? JustQuest.getProfileManager().getNPCConfig(npcID).getName() : npcName;

        this.opened = false;
        this.switching = false;

        Bukkit.getPluginManager().registerEvents(this, JustQuest.getInstance());
        this.start();
    }

    @Override
    public void start() {
        this.dialogue = conversation.getFirstDialogue(this.player);
        this.dialogue.fireEvents(this.player);
        this.lastChoiceOption = null;
        this.display();
    }

    @Override
    public void display() {
        if (dialogue == null) {
            this.end();
            player.closeInventory();

            ConversationEvent.Leave event = new ConversationEvent.Leave(player, npcID, conversation);
            event.call();
            return;
        }

        Scheduler.run(() -> {
            if (dialogue.getID().equals(conversation.getComplete())) {
                ConversationEvent.Complete event = new ConversationEvent.Complete(player, npcID, conversation);
                event.call();
            }

            dialogue.fireEvents(player);

            ConversationEvent.Contents event = new ConversationEvent.Contents(player, this.npcID, this.conversation, this.dialogue);
            event.call();

            JustQuest.getUiManager().openConversation(player, this.npcName, event.getDialogue(), !this.opened);
            if (!this.opened) this.opened = true;
            else this.switching = true;
        });
    }

    @Override
    public void nextDialogue(Player player, String key) {
        this.dialogue = this.conversation.getDialogue(player, key);
        if (this.dialogue == null && this.conversation.getComplete() == null) {
            ConversationEvent.Complete event = new ConversationEvent.Complete(player, npcID, conversation);
            event.call();
        }
    }

    @Override
    public void end() {
        PacketSender.sendRunFunction(player, "default", "global.hud_visible = true;", false);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onChoice(UIFCompSubmitEvent e) {
        Player who = e.getPlayer();
        if (!who.getUniqueId().equals(this.player.getUniqueId())) return;
        if (!e.getScreenID().equals(QuestUIManager.CONV_UI_ID)) return;

        OperateCode code = OperateCode.match(e.getParams().getParamI(0));
        if (code != OperateCode.Conv_Option) return;

        this.lastChoiceOption = e.getParams().getParam(1);
        ReplayOption option = this.dialogue.getOption(this.lastChoiceOption);

        ConversationEvent.Option event = new ConversationEvent.Option(player, this.npcID, this.conversation, this.dialogue.getID(), this.lastChoiceOption);
        event.call();
        if (event.isCancelled()) return;

        this.nextDialogue(player, option.getGo());
        this.display();

        option.fireEvents(player);
    }

    @EventHandler
    public void onClose(UIFScreenCloseEvent e) {
        Player who = e.getPlayer();
        if (!who.getUniqueId().equals(this.player.getUniqueId())) return;
        if (!e.getScreenID().equals(QuestUIManager.CONV_UI_ID)) return;

        if (this.switching) {
            this.switching = false;
            return;
        }

        this.end();

        ConversationEvent.Leave event = new ConversationEvent.Leave(player, npcID, conversation);
        event.call();
    }
}
