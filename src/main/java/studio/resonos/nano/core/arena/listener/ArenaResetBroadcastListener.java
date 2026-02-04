package studio.resonos.nano.core.arena.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import studio.resonos.nano.NanoArenas;
import studio.resonos.nano.api.event.ArenaResetEvent;
import studio.resonos.nano.core.managers.AdminAlertManager;
import studio.resonos.nano.core.util.CC;

public class ArenaResetBroadcastListener implements Listener {

    private final AdminAlertManager manager;

    public ArenaResetBroadcastListener(AdminAlertManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Check if alerts are enabled and if auto-enable is configured
        if (!NanoArenas.get().getConfigManager().isAlertsEnabled()) {
            return;
        }
        
        if (NanoArenas.get().getConfigManager().shouldAutoEnableAlertsOnJoin() && 
            event.getPlayer().hasPermission(NanoArenas.get().getConfigManager().getAlertsPermission())) {
            manager.setEnabled(event.getPlayer(), true);
            event.getPlayer().sendMessage(CC.translate(NanoArenas.get().getConfigManager().getMessagePrefix() + 
                NanoArenas.get().getConfigManager().getSuccessColor() + "Arena alerts enabled."));
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (manager.isEnabled(event.getPlayer())) {
            manager.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onArenaReset(ArenaResetEvent event) {
        // Check if alerts are enabled and if reset messages should be shown
        if (!NanoArenas.get().getConfigManager().isAlertsEnabled()) {
            return;
        }

        String arenaName = event.getArena().getName();
        long duration = event.getDurationMillis();
        long size = event.getSize();

        // Get message template and replace placeholders
        String messageTemplate = NanoArenas.get().getConfigManager().getResetMessageTemplate();
        String message = messageTemplate
                .replace("{arena}", arenaName)
                .replace("{size}", String.valueOf(size))
                .replace("{duration}", String.valueOf(duration));

        String finalMessage = CC.translate(
                NanoArenas.get().getConfigManager().getInfoColor() + message);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(NanoArenas.get().getConfigManager().getAlertsPermission())) continue;
            if (!manager.isEnabled(player)) continue;
            player.sendMessage(finalMessage);
        }
    }
}
