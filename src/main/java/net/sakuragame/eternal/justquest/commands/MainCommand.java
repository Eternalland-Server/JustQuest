package net.sakuragame.eternal.justquest.commands;

import com.taylorswiftcn.justwei.commands.JustCommand;
import net.sakuragame.eternal.justquest.commands.sub.*;

public class MainCommand extends JustCommand {

    public MainCommand() {
        super(new HelpCommand());
        register(new OpenCommand());
        register(new AllotCommand());
        register(new ReloadCommand());
    }
}
