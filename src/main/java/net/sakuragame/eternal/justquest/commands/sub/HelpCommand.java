package net.sakuragame.eternal.justquest.commands.sub;

import com.taylorswiftcn.justwei.commands.sub.SubCommand;
import net.sakuragame.eternal.justquest.commands.CommandPerms;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class HelpCommand extends SubCommand {

    private final String[] commands;

    public HelpCommand() {
        this.commands = new String[] {
                " §7/quest open - 打开任务界面",
                " §7/quest purge <player> - 清空玩家数据",
                " §7/quest allot <player> <quest id> - 分配任务",
                " §7/quest event <player> <event id> - 执行事件",
                " §7/quest reload - 重载配置文件"
        };
    }

    @Override
    public String getIdentifier() {
        return "help";
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        Arrays.stream(this.commands).forEach(sender::sendMessage);
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
