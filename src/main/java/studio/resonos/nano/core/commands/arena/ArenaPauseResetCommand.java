package studio.resonos.nano.core.commands.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.arena.Arena;
import studio.resonos.nano.core.util.CC;


public class ArenaPauseResetCommand {

    @Command(names = {"arena pause"}, permission = "nano.arena")
    public void Command(CommandSender sender, @Param(name = "arena") Arena arena, @Param(name = "action", required = false) String action) {
        if (arena == null) {
            sender.sendMessage(CC.RED + "An arena with that name does not exist.");
            return;
        }

        // default = toggle
        if (action == null || action.equalsIgnoreCase("toggle")) {
            boolean next = !arena.isAutoResetPaused();
            arena.setAutoResetPaused(next);
            arena.save();
            NanoArenas.get().getResetScheduler().schedule(arena);
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] " + (next ? "&aAuto-resets paused for arena " + arena.getName() + "."
                    : "&bAuto-resets resumed for arena " + arena.getName() + ".")));
            return;
        }

        if (action.equalsIgnoreCase("on")) {
            arena.setAutoResetPaused(true);
            arena.save();
            NanoArenas.get().getResetScheduler().schedule(arena);
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cAuto-resets paused for arena " + arena.getName() + "."));
            return;
        }

        if (action.equalsIgnoreCase("off")) {
            arena.setAutoResetPaused(false);
            arena.save();
            NanoArenas.get().getResetScheduler().schedule(arena);
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &aAuto-resets resumed for arena " + arena.getName() + "."));
            return;
        }

        sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &eUsage: /arena pause <arena> [on|off|toggle]"));
    }
}