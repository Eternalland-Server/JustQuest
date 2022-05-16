package net.sakuragame.eternal.justquest.core.conversation.io;

import com.taylorswiftcn.megumi.uifactory.event.comp.UIFCompSubmitEvent;
import com.taylorswiftcn.megumi.uifactory.event.screen.UIFScreenCloseEvent;
import ink.ptms.adyeshach.api.AdyeshachAPI;
import ink.ptms.adyeshach.common.entity.EntityInstance;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.conversation.Dialogue;
import net.sakuragame.eternal.justquest.core.conversation.ReplayOption;
import net.sakuragame.eternal.justquest.core.data.ExhibitNPC;
import net.sakuragame.eternal.justquest.ui.OperateCode;
import net.sakuragame.eternal.justquest.ui.QuestUIManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ExhibitConversationIO implements IConversationIO, Listener {

    private final Player player;
    private final ExhibitNPC npc;
    private Dialogue dialogue;

    public ExhibitConversationIO(Player player, ExhibitNPC npc) {
        this.player = player;
        this.npc = npc;

        Bukkit.getPluginManager().registerEvents(this, JustQuest.getInstance());
        this.start();
    }

    @Override
    public void start() {
        EntityInstance instance = AdyeshachAPI.INSTANCE.getEntityFromId(this.npc.getID(), this.player);
        if (instance == null) {
            this.end();
            return;
        }

        JustQuest.getUiManager().sendConvDoll(this.player, instance.getNormalizeUniqueId(), 3);
        this.dialogue = new Dialogue(
                "start",
                new ArrayList<String>() {{
                    add("&f你好，我是" + npc.getName());
                    add("");
                    add("");
                    add("");
                    addAll(npc.getDescriptions());
                }},
                new LinkedHashMap<String, ReplayOption>() {{
                    put("try", new ReplayOption("try", "&a点击试穿", new ArrayList<>(), null));
                    put("exit", new ReplayOption("exit", "&f离开", new ArrayList<>(), null));
                }}
        );
        this.display();
    }

    @Override
    public void display() {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_HARP, 0.33f, 1);
        JustQuest.getUiManager().openConversation(player, "&3&l" + npc.getName(), this.dialogue, false);
    }

    @Override
    public void nextDialogue(Player player, String key) {

    }

    @Override
    public void end() {
        PacketSender.sendRunFunction(player, "default", "global.hud_visible = true;", false);
        HandlerList.unregisterAll(this);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BELL, 0.33f, 1);
    }

    @EventHandler
    public void onChoice(UIFCompSubmitEvent e) {
        Player who = e.getPlayer();
        if (!who.getUniqueId().equals(this.player.getUniqueId())) return;
        if (!e.getScreenID().equals(QuestUIManager.CONV_UI_ID)) return;

        OperateCode code = OperateCode.match(e.getParams().getParamI(0));
        if (code != OperateCode.Conv_Option) return;

        String option = e.getParams().getParam(1);
        if (option.equals("try")) {
            JustQuest.getConversationManager().tryClothes(player, npc.getClothes());
        }

        this.end();
        player.closeInventory();
    }

    @EventHandler
    public void onClose(UIFScreenCloseEvent e) {
        Player who = e.getPlayer();
        if (!who.getUniqueId().equals(this.player.getUniqueId())) return;
        if (!e.getScreenID().equals(QuestUIManager.CONV_UI_ID)) return;

        this.end();
    }
}
