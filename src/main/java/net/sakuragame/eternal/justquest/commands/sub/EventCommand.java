package net.sakuragame.eternal.justquest.commands.sub;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.commands.CommandPerms;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class EventCommand extends SubCommand {
    @Override
    public String getIdentifier() {
        return "event";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) return;

        String s1 = args[0];
        String s2 = args[1];

        Player player = Bukkit.getPlayerExact(s1);
        if (player == null) return;

        JustQuest.getQuestManager().fireEvents(player, Collections.singletonList(s2));
        sender.sendMessage(ConfigFile.prefix + "已执行事件");
    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public String getPermission() {
        return CommandPerms.ADMIN.getNode();
    }
}
