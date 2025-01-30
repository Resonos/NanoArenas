package studio.resonos.nano.core.commands.arena;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.arena.impl.StandaloneArena;
import studio.resonos.nano.core.arena.selection.Selection;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaCreateCommand {

    @Command(names = {"arena create"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player player, @Param(name = "name") String arenaName) {

        if (Arena.getByName(arenaName) == null) {
            Selection selection = Selection.createOrGetSelection(player);

            if (selection.isFullObject()) {
                StandaloneArena arena = new StandaloneArena(arenaName, selection.getPoint1(), selection.getPoint2());
                Arena.getArenaNames().add(arena.getName());
                Arena.getArenas().add(arena);

                player.sendMessage(CC.translate("&bCreated new Arena&f " + arenaName));
            } else {
                player.sendMessage(CC.RED + "Your selection is incomplete.");
            }
        } else {
            player.sendMessage(CC.RED + "An arena with that name already exists.");
        }
    }
}
