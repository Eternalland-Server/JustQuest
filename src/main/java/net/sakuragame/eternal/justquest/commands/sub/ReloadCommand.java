package net.sakuragame.eternal.justquest.commands.sub;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justquest.JustQuest;
import net.sakuragame.eternal.justquest.commands.CommandPerms;
import net.sakuragame.eternal.justquest.file.sub.ConfigFile;
import net.sakuragame.eternal.justquest.util.Scheduler;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends SubCommand {

    @Override
    public String getIdentifier() {
        return "reload";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Scheduler.runAsync(() -> {
            JustQuest.getInstance().reload();
            sender.sendMessage(ConfigFile.prefix + "重载配置文件成功");
        });
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
