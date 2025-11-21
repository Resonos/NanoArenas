package studio.resonos.nano.core.commands.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaResetCommand {
    @Command(names = {"arena reset"}, permission = "nano.arena")
    public void Command(CommandSender sender, @Param(name = "arena") Arena arena) {
        if (arena != null) {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &aAttempting to reset arena " + arena.getName()));
            //long start = System.currentTimeMillis();
            arena.reset();
            //long end = System.currentTimeMillis();
           // sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &aSuccessfully reset arena " + arena.getName() + " in " + (end - start) + "ms"));
        } else {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cAn arena with that name does not exist.") );
        }
    }
}
