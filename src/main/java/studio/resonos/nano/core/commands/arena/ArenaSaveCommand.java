package studio.resonos.nano.core.commands.arena;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fastasyncworldedit.core.FaweAPI;

import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;

/**
 * @Author Athulsib
 * Package: studio.resonos.arenas.core.arena.command
 * Created on: 12/16/2023
 */
public class ArenaSaveCommand {

    @Command(names = {"arena save"}, permission = "nano.arena")
    public void Command(CommandSender sender) {
        for (Arena arena : Arena.getArenas()) {
            arena.save();
            FaweAPI.getTaskManager().async(() -> {
                arena.createSchematic();
            });
        }

        sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &aSaved " + Arena.getArenas().size() + " arenas."));
    }
}
