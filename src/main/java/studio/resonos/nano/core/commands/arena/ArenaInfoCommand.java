package studio.resonos.nano.core.commands.arena;

import org.apache.commons.lang3.StringEscapeUtils;
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
public class ArenaInfoCommand {
    @Command(names = {"arena info"}, permission = "nano.arena", playerOnly = true)
    public void Command(Player sender, @Param(name = "arena") Arena arena) {
        if (arena != null) {
            sender.sendMessage(CC.BLUE + CC.BOLD + "Arena Status " + CC.GRAY + "(" +
                    (arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + ")");

            sender.sendMessage(CC.GREEN + "Cuboid Lower Location: " + CC.YELLOW +
                    (arena.getLowerCorner() == null ?
                            StringEscapeUtils.unescapeJava("✗") :
                            StringEscapeUtils.unescapeJava("✓")));

            sender.sendMessage(CC.GREEN + "Cuboid Upper Location: " + CC.YELLOW +
                    (arena.getUpperCorner() == null ?
                            StringEscapeUtils.unescapeJava("✗") :
                            StringEscapeUtils.unescapeJava("✓")));

            sender.sendMessage(CC.GREEN + "Spawn Location: " + CC.YELLOW +
                    (arena.getSpawn() == null ?
                            StringEscapeUtils.unescapeJava("✗") :
                            StringEscapeUtils.unescapeJava("✓")));


        } else {
            sender.sendMessage(CC.RED + "An arena with that name does not exist.");
        }
    }
}
