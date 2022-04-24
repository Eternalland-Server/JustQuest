package net.sakuragame.eternal.justquest.commands.sub;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.commands.CommandPerms;
import net.sakuragame.eternal.justquest.core.user.QuestAccount;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PurgeCommand extends SubCommand {
    @Override
    public String getIdentifier() {
        return "purge";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 1) return;

        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) return;

        QuestAccount account = JustQuest.getAccountManager().getAccount(player.getUniqueId());
        account.purgeData();

        sender.sendMessage(ConfigFile.prefix + "数据清除成功");
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
