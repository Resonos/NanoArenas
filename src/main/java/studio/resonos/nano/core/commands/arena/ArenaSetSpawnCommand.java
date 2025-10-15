package studio.resonos.nano.core.commands.arena;

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
public class ArenaSetSpawnCommand {

    @Command(names = {"arena setspawn"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player player, @Param(name = "arena") Arena arena) {

        if (arena != null) {
            arena.setSpawn(player.getLocation());
            arena.save();
            arena.createSchematic();
            player.sendMessage(CC.BLUE + "Updated spawn point for arena " + arena.getName());
        } else {
            player.sendMessage(CC.RED + "An arena with that name does not exist.");
        }
    }
}
