package net.sakuragame.eternal.justquest.commands.sub;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.commands.CommandPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenCommand extends SubCommand {

    @Override
    public String getIdentifier() {
        return "open";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (args.length < 1) return;

        Player player = Bukkit.getPlayerExact(args[0]);
        if (player == null) return;
        JustQuest.getUiManager().openQuest(player);
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public String getPermission() {
        return CommandPerms.ADMIN.getNode();
    }
}
