package net.sakuragame.eternal.justquest.ui.component;

import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import com.taylorswiftcn.megumi.uifactory.generate.function.SubmitParams;
import com.taylorswiftcn.megumi.uifactory.generate.type.ActionType;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.TextureComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.core.conversation.ReplayOption;
import net.sakuragame.eternal.justquest.ui.OperateCode;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConvOptions {

    private final ScreenUI UI;
    private final Map<String, ReplayOption> options;

    public ConvOptions(Map<String, ReplayOption> options) {
        this.UI = new ScreenUI("conv_options");
        this.options = options;
    }

    public void send(Player player) {
        List<String> keys = new ArrayList<>(this.options.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String id = keys.get(i);
            ReplayOption option = options.get(id);

            String bg = id + "_bg";
            String text = id + "_text";
            UI
                    .addComponent(new TextureComp(bg)
                            .setTexture("ui/common/black_gradient_vip.png")
                            .setXY("options.x", "options.y+" + i * 21 + "*(w/960)")
                            .setCompSize("options.width", "18")
                            .setScale("(w/960)")
                            .addAction(ActionType.Enter, new Statements()
                                    .add("hover.x = " + bg + ".x-1*(w/960);")
                                    .add("hover.y = " + bg + ".y-1*(w/960);")
                                    .add("hover.visible = true;")
                                    .build()
                            )
                            .addAction(ActionType.Left_Click, text + ".scale = '0.95*(w/960)';")
                            .addAction(ActionType.Left_Release, text + ".scale = '(w/960)';")
                            .addAction(ActionType.Left_Release,
                                    new SubmitParams()
                                            .addValue(OperateCode.Conv_Option.getID())
                                            .addValue(id)
                            )
                    )
                    .addComponent(new LabelComp(text, option.getText())
                            .setX(bg + ".x+4*(w/960)+(" + text + ".width*(w/960)-" + text + ".width*" + text + ".scale)/2")
                            .setY(bg + ".y+4*(w/960)+(" + text + ".height*(w/960)-" + text + ".height*" + text + ".scale)/2")
                            .setScale("(w/960)")
                            .setExtend(bg)
                    );
        }

        PacketSender.sendYaml(player, FolderType.Gui, this.UI.getID(), this.UI.build(null));
    }
}
