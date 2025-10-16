package studio.resonos.nano.core.commands.arena;

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

    @Command(names = {"arena resetdelay"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player player, @Param(name = "arena") Arena arena, @Param(name = "delay") int delay) {
        if (arena != null) {
            arena.setResetTime(delay);
            arena.save();
            NanoArenas.get().getResetScheduler().schedule(arena);
            player.sendMessage(CC.BLUE + "Reset time for Arena " + arena.getName() + " set to " + delay + " seconds.");
        } else {
            player.sendMessage(CC.RED + "An arena with that name does not exist.");
        }
    }
}
