package studio.resonos.nano.core.commands.arena;

import org.bukkit.entity.Player;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.command.Command;
import studio.resonos.nano.api.command.paramter.Param;
import studio.resonos.nano.core.managers.AdminAlertManager;
import studio.resonos.nano.core.util.CC;

public class AlertsCommand {

    // usage: /alerts toggle  OR  /alerts on  OR  /alerts off
    @Command(names = {"nano alerts", "arena alerts"}, permission = "", playerOnly = true)
    public void Command(Player player, @Param(name = "action", required = false) String action) {
        // Check permission using configuration
        if (!player.hasPermission(NanoArenas.get().getConfigManager().getAlertsPermission())) {
            player.sendMessage(CC.translate(NanoArenas.get().getConfigManager().getErrorColor() + "You don't have permission to use this command."));
            return;
        }
        
        AdminAlertManager manager = NanoArenas.get().getManager();
        
        if (action == null || action.equalsIgnoreCase("toggle")) {
            boolean enabled = manager.toggle(player);
            player.sendMessage(CC.translate(enabled ? 
                NanoArenas.get().getConfigManager().getMessagePrefix() + NanoArenas.get().getConfigManager().getSuccessColor() + "Arena alerts enabled." : 
                NanoArenas.get().getConfigManager().getMessagePrefix() + NanoArenas.get().getConfigManager().getErrorColor() + "Arena alerts disabled."));
            return;
        }

        if (action.equalsIgnoreCase("on")) {
            manager.setEnabled(player, true);
            player.sendMessage(CC.translate(NanoArenas.get().getConfigManager().getMessagePrefix() + NanoArenas.get().getConfigManager().getSuccessColor() + "Arena alerts enabled."));
            return;
        }

        if (action.equalsIgnoreCase("off")) {
            manager.setEnabled(player, false);
            player.sendMessage(CC.translate(NanoArenas.get().getConfigManager().getMessagePrefix() + NanoArenas.get().getConfigManager().getErrorColor() + "Arena alerts disabled."));
            return;
        }

        player.sendMessage(CC.translate(NanoArenas.get().getConfigManager().getMessagePrefix() + NanoArenas.get().getConfigManager().getWarningColor() + "Usage: /alerts toggle|on|off"));
    }
}
