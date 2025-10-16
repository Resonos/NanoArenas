package studio.resonos.nano.core.arena.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        if (event.getPlayer().hasPermission("nano.alerts")) {
            manager.setEnabled(event.getPlayer(), true);
            event.getPlayer().sendMessage(CC.translate("&8[&bNanoArenas&8] &aArena alerts enabled."));
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
        String arenaName = event.getArena().getName();
        long duration = event.getDurationMillis();

        String message = CC.translate(
                "&8[&bNanoArenas&8] &fArena &b" + arenaName + "&f has been reset " + "(&e" + event.getSize() + "&f)" +"(&a" + duration + "ms&f).");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("nano.alerts")) continue;
            if (!manager.isEnabled(player)) continue;
            player.sendMessage(message);
        }
    }
}