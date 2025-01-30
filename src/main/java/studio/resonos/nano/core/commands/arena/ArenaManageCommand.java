package studio.resonos.nano.core.commands.arena;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.arena.menu.ArenaManagementMenu;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaManageCommand {
    @Command(names = {"arena manage"}, permission = "nano.arena")
    public void Command(Player sender) {
        ArenaManagementMenu.openMenu(sender);
    }
}