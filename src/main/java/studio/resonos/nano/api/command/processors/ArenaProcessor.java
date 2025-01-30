package studio.resonos.nano.api.command.processors;

import org.bukkit.command.CommandSender;
import studio.resonos.nano.api.command.paramter.Processor;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Athulsib
 * Package: studio.resonos.arenas.api.command.processors
 * Created on: 12/19/2023
 */
public class ArenaProcessor extends Processor<Arena> {

    @Override
    public Arena process(CommandSender sender, String supplied) {
        try {
            return Arena.getByName(supplied);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(CC.RED + "Unknown Arena: " + supplied);
            return null;
        }
    }

    // implement tab completions
    public List<String> tabComplete(CommandSender sender, String supplied) {
        List<String> arenaNames = Arena.getArenaNames();
        return arenaNames.stream()
                .filter(name -> name.toLowerCase().startsWith(supplied.toLowerCase()))
                .collect(Collectors.toList());
    }
}
