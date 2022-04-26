package net.sakuragame.eternal.justquest.ui.component;

import com.taylorswiftcn.megumi.uifactory.generate.function.Statements;
import com.taylorswiftcn.megumi.uifactory.generate.function.SubmitParams;
import com.taylorswiftcn.megumi.uifactory.generate.type.ActionType;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.LabelComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.component.base.TextureComp;
import com.taylorswiftcn.megumi.uifactory.generate.ui.screen.ScreenUI;
import net.sakuragame.eternal.dragoncore.config.FolderType;
import net.sakuragame.eternal.dragoncore.network.PacketSender;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.core.user.QuestProgress;
import net.sakuragame.eternal.justquest.ui.OperateCode;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class QuestList {

    private final ScreenUI UI;
    private final List<String> quests;

    public QuestList(List<String> quests) {
        this.UI = new ScreenUI("quest_list");
        this.quests = quests;
    }

    public void send(Player player) {
        if (this.quests.size() == 0) {
            PacketSender.sendYaml(player, FolderType.Gui, "quest_list", new YamlConfiguration());
            return;
        }

        QuestAccount account = JustQuest.getAccountManager().getAccount(player);

        for (int i = 0; i < quests.size(); i++) {
            String id = this.quests.get(i);
            IQuest quest = JustQuest.getProfileManager().getQuest(id);
            QuestProgress progress = account.getQuestProgress().get(id);

            UI
                    .addComponent(new TextureComp("q_" + i)
                            .setTexture("(global.quest_choose == '" + id + "')?'ui/quest/line.png':'0,0,0,0'")
                            .setXY("area.x", "area.y+" + i * 26)
                            .setCompSize(177, 25)
                            .addAction(ActionType.Left_Click, new SubmitParams()
                                    .setCondition("(global.quest_choose != '" + id + "')")
                                    .addValue(OperateCode.Choose_Quest.getID())
                                    .addValue(id)
                            )
                            .addAction(ActionType.Left_Click, new Statements()
                                    .add("func.Sound_Play();")
                                    .add("global.quest_choose = '" + id + "';")
                                    .build()
                            )
                    )
                    .addComponent(new TextureComp("q_" + i + "_a")
                            .setTexture("(global.quest_choose == '" + id + "')?'ui/quest/ss.png':'ui/quest/s.png'")
                            .setXY("q_" + i + ".x+1", "q_" + i + ".y+1")
                            .setCompSize(23, 23)
                            .setExtend("q_" + i)
                    )
                    .addComponent(new TextureComp("q_" + i + "_b")
                            .setTexture(quest.getType().getTexture())
                            .setXY("q_" + i + "_a.x+6.5", "q_" + i + "_a.y+5")
                            .setCompSize(10, 13)
                            .setExtend("q_" + i)
                    )
                    .addComponent(new LabelComp("q_" + i + "_c", quest.getName())
                            .setXY("q_" + i + ".x+28", "q_" + i + ".y+7")
                            .setScale(1.2)
                            .setExtend("q_" + i)
                    );

            if (progress.isCompleted()) {
                UI.addComponent(new TextureComp("q_" + i + "_d")
                        .setTexture("ui/quest/done.png")
                        .setXY("q_" + i + ".x+144", "q_" + i + ".y+5.5")
                        .setCompSize(27, 14)
                );
            }
        }

        PacketSender.sendRunFunction(player, "default", "global.quest_choose = '" + quests.get(0) + "';", false);
        PacketSender.sendYaml(player, FolderType.Gui, this.UI.getID(), this.UI.build(null));
    }
}
