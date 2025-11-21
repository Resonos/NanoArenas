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
public class ArenaResetTimeCommand {

    @Command(names = {"arena resetdelay"}, permission = "nano.arena")
    public void Command(CommandSender sender, @Param(name = "arena") Arena arena, @Param(name = "delay") int delay) {
        if (arena != null) {
            arena.setResetTime(delay);
            arena.save();
            NanoArenas.get().getResetScheduler().schedule(arena);
            sender.sendMessage(CC.BLUE + "Reset time for Arena " + arena.getName() + " set to " + delay + " seconds.");
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &fReset time for arena &b" + arena.getName() + "&f set to &b" + delay + " &fseconds."));
        } else {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cAn arena with that name does not exist.") );
        }
    }
}
