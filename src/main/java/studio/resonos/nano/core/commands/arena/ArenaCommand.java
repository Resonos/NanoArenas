package studio.resonos.nano.core.commands.arena;

import org.bukkit.command.CommandSender;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.util.CC;

/**
 * @Author: Athulsib
 * Package: studio.resonos.arenas.core.commands.arena
 * Created on: 12/25/2023
 */
public class ArenaCommand {

    @Command(names = {"arena help", "arena"}, permission = "", description = "Shows NanoArenas help")
    public void Command(CommandSender sender) {
        // Check permission using configuration
        if (!sender.hasPermission(NanoArenas.get().getConfigManager().getArenaPermission())) {
            sender.sendMessage(CC.translate(NanoArenas.get().getConfigManager().getMessagePrefix() + 
                NanoArenas.get().getConfigManager().getErrorColor() + "You don't have permission to use this command."));
            return;
        }
        
        sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &f" + " &b&lNano Arenas"));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate(" &fThis server is running &bNano. A lightning fast Arena system"));
        sender.sendMessage(CC.translate(" &fmade for Large scale maps and servers."));
        sender.sendMessage(" ");
        sender.sendMessage(CC.translate(" &fDeveloped by &bResonos Studios &f[&bdsc.gg/resonos&f]"));
        sender.sendMessage(CC.CHAT_BAR);
    }
}
