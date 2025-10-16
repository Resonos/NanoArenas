package studio.resonos.nano.core.commands.arena;

import org.bukkit.entity.Player;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaCommand {

    @Command(names = {"arena help", "arena"}, permission = "nano.arena")
    public void Command(Player sender) {
        sender.sendMessage(CC.translate("&b&lArena &7&m-&r &b&lHelp"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena create &8<&7arena&8>"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena delete &8<&7arena&8>"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena info &8<&7arena&8>"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena setspawn &8<&7arena&8>"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena teleport &8<&7arena&8>"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena seticon &8<&7arena&8>"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena manage"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arena save"));
        sender.sendMessage(CC.translate(" &7⚙ &b/arenas"));
    }
}