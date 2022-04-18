package net.sakuragame.eternal.justquest.ui.component;

import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.EntityViewComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ConvDoll {

    private final ScreenUI UI;
    private final UUID uuid;
    private final double scale;

    public ConvDoll(UUID uuid, double scale) {
        this.UI = new ScreenUI("conv_doll");
        this.uuid = uuid;
        this.scale = scale;
    }

    public void send(Player player) {
        this.UI
                .addComponent(new EntityViewComp("doll", uuid.toString())
                        .setShowHead(false)
                        .setXY("doll_bg.x+(doll_bg.width*doll_bg.scale/2)", "doll_bg.y+220*(w/960)")
                        .setScale(scale + "*(w/960)")
                );

        PacketSender.sendYaml(player, FolderType.Gui, this.UI.getID(), this.UI.build(null));
    }
}
