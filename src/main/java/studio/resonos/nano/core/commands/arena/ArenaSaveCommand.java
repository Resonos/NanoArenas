package studio.resonos.nano.core.commands.arena;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.arena.Arena;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaSaveCommand {

    @Command(names = {"arena save"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player sender) {
        for (Arena arena : Arena.getArenas()) {
            arena.save();
        }

        sender.sendMessage(ChatColor.GREEN + "Saved all arenas!");
    }
}
