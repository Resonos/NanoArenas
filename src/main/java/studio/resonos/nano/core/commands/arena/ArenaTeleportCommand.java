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
public class ArenaTeleportCommand {

    @Command(names = {"arena teleport"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player sender, @Param(name = "arena") Arena arena) {
        if (arena == null) {
            sender.sendMessage(CC.translate("&cAn arena with that name does not exist."));
        } else {
            sender.teleport(arena.getSpawn());
            sender.sendMessage(CC.translate("&aYou have been teleported to Arena " + arena.getName()));
        }
    }
}
