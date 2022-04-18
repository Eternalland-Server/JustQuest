package net.sakuragame.eternal.justquest.commands;

import com.taylorswiftcn.justwei.commands.JustCommand;
import net.sakuragame.eternal.justquest.api.event.AllotCommand;
import net.sakuragame.eternal.justquest.commands.sub.HelpCommand;
import net.sakuragame.eternal.justquest.commands.sub.OpenCommand;
import net.sakuragame.eternal.justquest.commands.sub.ReloadCommand;

public class MainCommand extends JustCommand {

    public MainCommand() {
        super(new HelpCommand());
        register(new OpenCommand());
        register(new AllotCommand());
        register(new ReloadCommand());
    }
}
