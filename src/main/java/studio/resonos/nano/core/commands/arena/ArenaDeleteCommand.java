package studio.resonos.nano.core.commands.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaDeleteCommand {
    @Command(names = {"arena delete"}, permission = "nano.arena", playerOnly = true)
    public void Command(CommandSender sender, @Param(name = "arena") Arena arena) {
        if (arena != null) {
            arena.delete();
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &fDeleted arena &b" + arena.getName() + "&f."));
        } else {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cAn arena with that name does not exist."));
        }
    }
}
