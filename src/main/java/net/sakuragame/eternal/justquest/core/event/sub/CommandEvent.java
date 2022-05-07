package net.sakuragame.eternal.justquest.core.event.sub;

import net.sakuragame.eternal.justquest.core.event.AbstractEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandEvent extends AbstractEvent {

    private final Mode mode;
    private final List<String> commands;

    public CommandEvent(String ID, ConfigurationSection section) {
        super(ID, section);
        this.mode = Mode.valueOf(section.getString("mode", "PLAYER").toUpperCase());
        this.commands = section.getStringList("commands");
    }

    @Override
    public void execute(Player player) {
        if (this.commands.isEmpty()) return;

        switch (mode) {
            case CONSOLE:
                this.consoleExecute(player);
                return;
            case OP:
                this.opExecute(player);
                return;
            case PLAYER:
                this.playerExecute(player);
        }
    }

    private void consoleExecute(Player player) {
        this.commands.forEach(cmd ->
                Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        cmd.replace("%player%", player.getName()))
        );
    }

    private void opExecute(Player player) {
        if (player.isOp()) {
            this.commands.forEach(s -> player.performCommand(s.replace("%player%", player.getName())));
            return;
        }

        try {
            player.setOp(true);
            this.commands.forEach(s -> player.performCommand(s.replace("%player%", player.getName())));
        }
        catch (Exception ignore) {}
        finally {
            player.setOp(false);
        }
    }

    private void playerExecute(Player player) {
        this.commands.forEach(s -> player.performCommand(s.replace("%player%", player.getName())));
    }

    public enum Mode {
        PLAYER,
        OP,
        CONSOLE
    }
}
