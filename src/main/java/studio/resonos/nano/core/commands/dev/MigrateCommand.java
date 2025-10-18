package studio.resonos.nano.core.commands.dev;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.arena.menu.ArenaManagementMenu;
import studio.resonos.nano.core.arena.menu.MigratorMenu;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class MigrateCommand {
    @Command(names = {"nano migrate","arena migrate"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player sender) {
        MigratorMenu.OpenMenu(sender);
    }
}