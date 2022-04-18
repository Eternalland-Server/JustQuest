package net.sakuragame.eternal.justquest.api.event;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.commands.CommandPerms;
import net.sakuragame.eternal.justquest.core.quest.IQuest;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AllotCommand extends SubCommand {
    @Override
    public String getIdentifier() {
        return "allot";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 2) return;

        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) return;

        IQuest quest = JustQuest.getProfileManager().getQuest(args[1]);
        if (quest == null) return;

        quest.allot(player.getUniqueId());
        sender.sendMessage(ConfigFile.prefix + "allot success");
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
