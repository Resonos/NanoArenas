package studio.resonos.nano.core.commands.dev;

import org.bukkit.command.CommandSender;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.core.util.CC;

/**
 * Reload command for NanoArenas configuration
 */
public class ReloadCommand {

    @Command(names = {"nano reload", "arena reload", "nanoadmins reload"}, 
               permission = "nano.reload", 
               description = "Reload NanoArenas configuration")
    public void onReload(CommandSender sender) {
        long startTime = System.currentTimeMillis();
        
        sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &eReloading configuration..."));
        
        try {
            // Reload the configuration
            NanoArenas.get().reloadConfiguration();
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &aConfiguration reloaded successfully!"));
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &7Reload completed in &e" + duration + "ms&7."));
            
            // Log to console as well
            NanoArenas.get().getLogger().info("Configuration reloaded by " + sender.getName() + " in " + duration + "ms");
            
        } catch (Exception e) {
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cFailed to reload configuration!"));
            sender.sendMessage(CC.translate("&8[&bNanoArenas&8] &cError: " + e.getMessage()));
            
            NanoArenas.get().getLogger().severe("Failed to reload configuration: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
