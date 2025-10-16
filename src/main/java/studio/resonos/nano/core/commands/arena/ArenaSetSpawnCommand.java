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
            player.sendMessage(CC.translate("&8[&bNanoArenas&8] &fSet spawn for arena &b" + arena.getName() + " &fto your current location."));
            player.sendMessage(CC.translate("&8[&bNanoArenas&8] &eFinalizing arena &b" + arena.getName() + "..."));
            arena.createSchematic();
            player.sendMessage(CC.translate("&8[&bNanoArenas&8] &aSuccessfully finalized arena &b" + arena.getName() + "&a."));
        } else {
            player.sendMessage(CC.translate("&8[&bNanoArenas&8] &cAn arena with that name does not exist."));
        }
    }
}
