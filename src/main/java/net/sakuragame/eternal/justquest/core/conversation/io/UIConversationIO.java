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

    private String npcName;
    private Dialogue dialogue;

    private boolean opened;
    private boolean switching;

    public UIConversationIO(Player player, String npcID, Conversation conversation) {
        this.player = player;
        this.npcID = npcID;
        this.conversation = conversation;

        this.opened = false;
        this.switching = false;

        Bukkit.getPluginManager().registerEvents(this, JustQuest.getInstance());
        this.start();
    }

    @Override
    public void start() {
        this.npcName = JustQuest.getProfileManager().getNPCConfig(this.npcID).getName();
        this.dialogue = conversation.getFirstDialogue();
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

            JustQuest.getUiManager().openConversation(player, this.npcName, this.dialogue, !this.opened);
            if (!this.opened) this.opened = true;
            else this.switching = true;
        });
    }

    @Override
    public void setDialogue(String key) {
        this.dialogue = this.conversation.getDialogue(key);
        if (this.dialogue == null && this.conversation.getComplete().isEmpty()) {
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
    public void onSelOption(UIFCompSubmitEvent e) {
        Player who = e.getPlayer();
        if (!who.getUniqueId().equals(this.player.getUniqueId())) return;
        if (!e.getScreenID().equals(QuestUIManager.CONV_UI_ID)) return;

        OperateCode code = OperateCode.match(e.getParams().getParamI(0));
        if (code != OperateCode.Conv_Option) return;

        String id = e.getParams().getParam(1);
        ReplayOption option = this.dialogue.getOption(id);
        option.fireEvents(player);

        this.setDialogue(option.getGo());
        this.display();
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
